FastApproval [![Build Status](https://secure.travis-ci.org/nikolavp/approval.png)](https://travis-ci.com/maxbechtold/approval) [![Documentation Status](https://readthedocs.org/projects/approval/badge/?version=latest)](https://readthedocs.org/projects/approval/?badge=latest)
=======
Approval is an open source assertion/verification library to aid unit testing. *FastApproval* makes it even more fun. 

This **fork** of @nicolavp/approval aims at maintaining the library for future releases of Java. 

How is approval testing different
=================================

There are many sources from which you can learn about approval testing(just google it) but basically the process is the following:

1. you already have a working implementation of the thing you want to test
2. you run it and get the result the first time
3. the result will be shown to you in your preferred tool(this can be configured)
4. you either approve the result in which case it is recored(saved) and the test pass or you disapprove it in which case the test fails
5. the recorded result is then used on further test runs to make sure that there are no regressions in your code(i.e. you broke something and the result is not the same).
6. Of course sometimes you want to change the way something behaves so if the result is not the same we will prompt you with difference between the new result and the last recorded again in your preferred tool.

Want to learn more? See **[our latest documentation](http://approval.readthedocs.org/en/latest/)**.

The current project is using ideas from [ApprovalTests](https://github.com/approvals/ApprovalTests.Java) but should provide a more up to date java feel to it.

Dependencies
---
There are currently no dependencies for the project

## LICENSE
[Apache 2.0 License](https://github.com/maxbechtold/approval/blob/master/LICENSE.txt)


Questions or Issues?
---
You can just file an issue, write a bug report or create a pull request with a patch :)


Documentation
-----
Documentation is never enough. Wanna write some of it and see how it looks? It is super easy - just install sphinx-autobuild and run:
```
sphinx-autobuild -b html doc/source doc/build/html
```

from the root directory of the project
