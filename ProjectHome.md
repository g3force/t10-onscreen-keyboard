# Summary #
This is an intelligent and dynamic keyboard with auto-completion and personalization.

It is primary intended for handicapped people who can not use a physical keyboard. The aim of the keyboard is, to provide a method of fast typing. For this reason and against the concept of most other keyboards, you only receive the most likely suggestion.

One of the main features is the profile management. You can have profiles for different kinds of context, e.g. different languages or formal/informal. Also, the layout can be based on profiles.

As everybody has another behavior of typing, you can have your personal dictionaries. They will be extended and prioritized while you type.
You can create a basis for a dictionary by reading arbitrary text. The priority is based on the number of occurrences of the words.

A great advantage of this on-screen keyboard is the compatibility between Linux and Windows.

# Installation #
## Ubuntu PPA ##
If you are running Ubuntu 10.04 or later, we recommend to use our PPA.
Just run following commands in a terminal:

```
sudo add-apt-repository ppa:fit42/t10
sudo apt-get update
sudo apt-get install t10-keyboard
```

More details can be found on this page:
https://code.launchpad.net/~fit42/+archive/t10

## Windows Executable ##
If you use Windows, you might prefer an executable to a JAR file.
This files are called `t10-keyboard-<version>.exe`. They are just a wrapper over the according JAR files, not an installer!

## Executable JAR ##
Download the latest JAR file from the download section and just run it.
This file is compatible to both Windows and Linux.

## Windows Installer ##
The Windows Installer is the easiest and most comfortable way of installing the keyboard. Just download the installer and run it. It will create a shortcut on your desktop to start the keyboard.

Note, that the installer might not always be the latest version.

## Compile it yourself ##
We provide the full sourcecode in our repository. Just clone it according to the instructions and run `ant pack-jar`.


# Screenshots #
![http://wiki.t10-onscreen-keyboard.googlecode.com/git-history/master/Screenshot_Keyboard_111207.png](http://wiki.t10-onscreen-keyboard.googlecode.com/git-history/master/Screenshot_Keyboard_111207.png)