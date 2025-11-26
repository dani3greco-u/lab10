#Work with MVC

This application is the same as the main course slides, with small improvements (the code here does not need to fit on a few slides). Refer to the slides to understand how the application works.
We will enrich the example by adding I/O.

## Prepare the UI to show errors

I/O access is a source of new possible unexpected situations, which should be reported to the user as errors via the user interface. As a first step, we will add to the view the ability to display errors generated in the controller.

* Add a `void displayError(String message)` method in `DrawNumberView`
* Implement the method in `DrawNumberViewImpl`, using `JOptionPane.showMessageDialog()` to display the error

##Configuration file

Currently, the game settings (minimum, maximum and number of attempts allowed) are hardcoded.
Modify the application so that these settings are read from the provided configuration file.
The default configuration file is available in the path `src/main/resources/config.yml`.

### Tips

* Use the resource loader to load the provided file into res.
  You can wrap the stream with a `StreamReader` and a `BufferedReader` to get the contents and proceed line by line.
* The file (see the example in the "res" folder) is a [standard YAML file](https://en.wikipedia.org/wiki/YAML).
  There are libraries to easily parse YML, but this file itself is so simple that no particular strategy is required:
  just filter what's before and after the colon.
* Don't run file system view.
  More precisely, the controller in its constructor must be able to read the provided configuration file,
  and import the three constants.
  Even better,
  an external utility (a method or another class) may be responsible.
  The reason is that interaction with the file system is not part of the domain model, nor, in this case, the view.
* You can use `StringTokenizer` to split a `String` into parts.
  Another option, available just for this case, is `String.split()`, passing `":"` as the argument.
  Be careful with `split()`: the `String` input is actually a regular expression.
  Regular expressions (regex) are a very powerful tool, but we can't cover them in this course:
  ``StringTokenizer`` is recommended unless you already know regular expressions and how to use them.

## Multiple views

* Note that the architecture allows multiple views to be attached at runtime.
* Attach two graphical views, the file logger and the console view, at the same time and verify that the application works as expected