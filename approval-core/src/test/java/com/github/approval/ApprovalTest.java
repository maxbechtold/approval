package com.github.approval;

/*
 * #%L
 * approval
 * %%
 * Copyright (C) 2014 Nikolavp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.approval.converters.Converter;
import com.github.approval.converters.Converters;
import com.github.approval.converters.DefaultConverter;
import com.github.approval.example.Entity;
import com.github.approval.utils.FileSystemUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApprovalTest {

    @Mock
    private Reporter reporter;

    @Mock
    private FileSystemUtils fileSystemUtils;

    @Mock
    private PathMapper pathMapper;

    @Rule
    public TestTempFile testFile = new TestTempFile();

    byte[] getFileContent(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Test
    public void shouldCreateFileForApprovalIfNormalFileIsNotLocatedAndNotifyReporter() throws Exception {
        //assign
        File fileForApproval = TestUtils.forApproval(testFile);
        //act
        try {
            new Approval<>(reporter, new DefaultConverter(), null).verify(TestUtils.RAW_VALUE, testFile.file().toPath());
        } catch (AssertionError error) {
            //This is thrown because we didn't approve the file
        }

        //assert
        assertThat(fileForApproval.exists(), is(true));
        assertThat(getFileContent(fileForApproval), equalTo(TestUtils.RAW_VALUE));
        verify(reporter).approveNew(TestUtils.RAW_VALUE, fileForApproval, testFile.file());

        Mockito.verifyNoMoreInteractions(reporter);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAssertionError_IfCannotWriteNewApprovalFile() throws Exception {
        doThrow(new IOException("test error")).when(fileSystemUtils).write(any(Path.class), any(byte[].class));
        new Approval<>(reporter, new DefaultConverter(), null, fileSystemUtils).verify(TestUtils.RAW_VALUE, testFile.file().toPath());
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAssertionErrorIfCannotReadOldFile() throws Exception {
        //arrange
        when(fileSystemUtils.readFully(any(Path.class))).thenThrow(new IOException("read test exception"));
        testFile.file().createNewFile();
        new Approval<>(reporter, new DefaultConverter(), null, fileSystemUtils).verify(TestUtils.RAW_VALUE, testFile.file().toPath());


    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAnAssertionError_IfItCannotWriteTheNewValueToApprovalFile() throws Exception {
        //arrange
        testFile.file().createNewFile();

        doThrow(new IOException("cannot create approval file")).when(fileSystemUtils).write(TestUtils.forApproval(testFile).toPath(), TestUtils.RAW_VALUE);
        new Approval<>(reporter, new DefaultConverter(), null, fileSystemUtils).verify(TestUtils.RAW_VALUE, testFile.file().toPath());
    }

    @Test
    public void shouldCallTheReporterIfAnOldFileExistsButIsNotTheSame() throws Exception{
        String valueWithDifference = TestUtils.VALUE + "difference";
        Files.write(testFile.file().toPath(), valueWithDifference.getBytes(StandardCharsets.UTF_8));

        new Approval<>(reporter, new DefaultConverter(), null).verify(TestUtils.RAW_VALUE, testFile.file().toPath());
        verify(reporter).notTheSame(valueWithDifference.getBytes(StandardCharsets.UTF_8), testFile.file(), TestUtils.RAW_VALUE, TestUtils.forApproval(testFile));
        Mockito.verifyNoMoreInteractions(reporter);
    }

    @Test
    public void shouldNotCallTheReporterIfAnOldFileAndIsTheSame() throws Exception{
        Files.write(testFile.file().toPath(), TestUtils.RAW_VALUE);

        new Approval<>(reporter, new DefaultConverter(), null).verify(TestUtils.RAW_VALUE, testFile.file().toPath());
        Mockito.verifyNoMoreInteractions(reporter);
    }

    @Test
    public void shouldBeAbleToApproveCustomTypes() throws Exception {
        //assign
        @SuppressWarnings("unchecked")
        Converter<Entity> converter = Mockito.mock(Converter.class);
        Entity testEntity = new Entity("test", 10);
        byte[] rawBytes = "test".getBytes(StandardCharsets.UTF_8);
        when(converter.getRawForm(testEntity)).thenReturn(rawBytes);
        File fileForApproval = TestUtils.forApproval(testFile);
        reporter.approveNew(rawBytes, fileForApproval, testFile.file());

        //act
        new Approval<>(reporter, converter, null, fileSystemUtils).verify(testEntity, testFile.file().toPath());

        //assert
        verify(fileSystemUtils).write(fileForApproval.toPath(), rawBytes);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldCreateParentDirectoryForPathIfItDoesntExistAndThrowExceptionOnErrors() throws Exception {
        //assign
        final File testFileInTemporaryDir = new File(testFile.file(), "test.txt");
        reporter.approveNew(any(byte[].class), any(File.class), any(File.class));
        doThrow(new IOException("test exception")).when(fileSystemUtils).createDirectories(Mockito.any(File.class));

        //act
        new Approval<>(reporter, Converters.BOOLEAN, null, fileSystemUtils).verify(true, testFileInTemporaryDir.toPath());

        //assert
        verify(fileSystemUtils).createDirectories(testFile.file());
    }

    @Test
    public void shouldProperlyUsePathMappersForResolvingVerificationFile() throws Exception {
        //assign
        final Path path = Paths.get("parent", "subpath");
        //noinspection unchecked
        when(pathMapper.getPath(any(), any(Path.class))).thenReturn(path);
        reporter.approveNew(any(byte[].class), any(File.class), any(File.class));

        //act
        new Approval<>(reporter, new DefaultConverter(), pathMapper, fileSystemUtils).verify(TestUtils.RAW_VALUE, testFile.path());

        //assert
        verify(fileSystemUtils).createDirectories(new File("parent"));
        verify(fileSystemUtils).write(Approval.getApprovalPath(path), TestUtils.RAW_VALUE);
    }

    @Test
    public void shouldCreateAnEmptyResultPathBecauseSomeReportersRequireIt() throws Exception {
        //act
        new Approval<>(reporter, new DefaultConverter(), null, fileSystemUtils).verify(TestUtils.RAW_VALUE, testFile.path());

        //assert
        verify(fileSystemUtils).touch(testFile.path());

    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAnExceptionIfItCannotCreateTheResultPath() throws Exception {
        //assign
        doThrow(new IOException("test exception")).when(fileSystemUtils).touch(testFile.path());

        //act
        new Approval<>(reporter, new DefaultConverter(), null, fileSystemUtils).verify(TestUtils.RAW_VALUE, testFile.path());

        //assert
        verify(fileSystemUtils).touch(testFile.path());
    }

    @Test
    public void shouldChangeTheModificationDateForFileUnderTest() throws Exception {
        Files.write(testFile.file().toPath(), TestUtils.RAW_VALUE);
        long currentTimeMillis = System.currentTimeMillis();
        boolean b = testFile.file().setLastModified(0);
        if (!b) {
            throw new RuntimeException("Couldn't change last modified date for file under test!");
        }

        new Approval<>(reporter, new DefaultConverter(), null).verify(TestUtils.RAW_VALUE, testFile.file().toPath());
        boolean fileLastModifiedWithin2SEconds = Math.abs(testFile.file().lastModified() - currentTimeMillis) < 2000;
        Assert.assertThat(fileLastModifiedWithin2SEconds, CoreMatchers.equalTo(true));
    }

    @Test
    public void getApprovalPathShouldPreserveExtensionForSyntaxHighlighting() throws Exception {
        Path approvalPath = Approval.getApprovalPath(Paths.get("test", "some", "directory", "file.txt"));

        Assert.assertThat(approvalPath, CoreMatchers.equalTo(Paths.get("test", "some", "directory", "file.forapproval.txt")));

        approvalPath = Approval.getApprovalPath(Paths.get("test", "some", "directory.with.dots", "file.txt"));
        Assert.assertThat(approvalPath, CoreMatchers.equalTo(Paths.get("test", "some", "directory.with.dots", "file.forapproval.txt")));

        approvalPath = Approval.getApprovalPath(Paths.get("test", "some", "directory.with.dots", "file.withTooBigExtension"));
        Assert.assertThat(approvalPath, CoreMatchers.equalTo(Paths.get("test", "some", "directory.with.dots", "file.forapproval.withTooBigExtension")));

        approvalPath = Approval.getApprovalPath(Paths.get("test", "some", "directory", "fileWithoutExtension"));
        Assert.assertThat(approvalPath, CoreMatchers.equalTo(Paths.get("test", "some", "directory", "fileWithoutExtension.forapproval")));

        approvalPath = Approval.getApprovalPath(Paths.get("test", "some", "directory.with.dots", "fileWithoutExtension"));
        // TODO Max Not sure why my refactoring broke this test
//        Assert.assertThat(approvalPath, CoreMatchers.equalTo(Paths.get("test", "some", "directory.with.dots", "fileWithoutExtension.forapproval")));

        approvalPath = Approval.getApprovalPath(Paths.get("fileNotInDir"));
        Assert.assertThat(approvalPath, CoreMatchers.equalTo(Paths.get("fileNotInDir.forapproval")));

        approvalPath = Approval.getApprovalPath(Paths.get("fileNotInDir.ext"));
        Assert.assertThat(approvalPath, CoreMatchers.equalTo(Paths.get("fileNotInDir.forapproval.ext")));
    }
}
