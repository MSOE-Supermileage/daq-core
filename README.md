### Overview ###

This project is for the Milwaukee School of Engineering SuperMileage Team, a subset of SAE. The goal is to build a functional data acquisition system for Prototype Electric and Gas Vehicles for compete in the [Shell Eco-Marathon](http://www.shell.com/global/environment-society/ecomarathon/events/americas.html) and [SAE Supermileage](http://students.sae.org/cds/supermileage/) collegiate design and engineering competitions.
The system consists of an android phone as a HUD (heads up display) USB Tethered to a [Raspberry PI](https://www.raspberrypi.org/) that collects data on various sensors connected to the GPIO.
Check out the [Task Board](https://waffle.io/MSOE-Supermileage/DAQ).

### Setup ###

#### Tools/Kits ####
* [IntelliJ IDEA](http://www.jetbrains.com/idea/)
* [Java Development Kit 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [Android SDK](https://developer.android.com/sdk/index.html)
* [Python2.7](https://docs.python.org/2/)

#### Configuration ####
* SDK Version 21 or later for android 5.0.1
* The android portion of the project is currently out of date. Deploy scripts are needed to be written to deploy to a clean install of raspbian, and code will need to be imported to github.
* The Web Server Project is a windows application. The connection between the heads up display and the web server is not currenlty functional.

# docs

This is the gathering ground for much of the documentation related to the MSOE-Supermileage organization.

While this is considered a catch-all, putting your documentation for a specific system in that system's repo is high encouraged.

## Git

Nearly everything you could possibly want to know about Git can be found in the [Pro Git book](http://www.git-scm.com/book/en/) and the [Reference manual](http://www.git-scm.com/docs).

### Workflow

We (and you) shall use the wonderful [Feature Branch Workflow][feature-branch-workflow]. This means you ***never*** push directly to the master branch. That being said, there are exceptions:
  - Fixing a typo
  - Changing a non-crucial text file (e.g. README)
  - Changing less than 5 lines of trivial code

Here is the gist of the feature branch workflow. The master branch will always be a deployable version (i.e. a minimum viable product); it will compile and work correctly as specified by the requirements. Any new features, bug fixes, etc. will be developed in their own branch. Since these features will probably be worked on by multiple people, you will want to make sure to push these to the remote git server.

#### Example

1. Create a new feature to work on:

   ```bash
   $ git branch <branch-feature-name>           # Create a new branch
   $ git checkout <branch-feature-name>         # Switch to the specified branch
   # These two steps can be simplified to
   $ git checkout -b <branch-feature-name>
   ```
    
1. Do something, like commit:
   
   ```bash
   $ git commit ./path/to/file/to/commit -m '<commit message>'
   ```
   
1. Push to the remote server, so that your changes are not lost forever and so other people can work on your code too:
   
   ```bash
   $ git push -u origin <branch-feature-name>
   ```
   
   You only need the `-u` the first time the branch is pushed; afterwards, you should be able to simply `git push`.

#### Merging vs. Rebasing

Use `git rebase` instead of `git merge`, and `git pull -r` instead of `git pull`.

Rebasing is a complex, magic process. It essentialy makes the Git log/history more linear (i.e. there are fewer diverging branches and merges). It makes the history of commits easier to read.

For more information see [Atlassian's Merging vs. Rebasing][merge-vs-rebase].

#### Tagging

When a new version of the software is extensively tested on the vehicle(s), or when it is used at competition, you should tag the commit that corresponds to the deployed build. This will help us track how well each version worked.

A basic example of tagging:

```bash
$ git tag -a v0.1.0-beta -m 'some brief description of this tag' [<commit>]
$ git push origin --tags
```

The first command makes an annotated tag with a description. If the SHA-1 of a commit is provided for `<commit>`, the tag will added to that commit instead of the current commit (HEAD). The second command will push all local tags to the remote server.

[feature-branch-workflow]: https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow
[merge-vs-rebase]: https://www.atlassian.com/git/tutorials/merging-vs-rebasing

