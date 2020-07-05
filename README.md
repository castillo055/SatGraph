# SatGraph
Simple application that shows satellite orbits around earth.

## Dependencies
This is a very rudimentary project so it does not include any depencency management configuration. However, it does depend on the following:
- JavaFX: It has been tested with JavaFX 11.0.2 but it should work with other versions as well.
- [KBControllsFX](https://github.com/castillo055/KBControlsFX): Little library I created to facilitate setting up keyboard control in JavaFX apps.

If you choose to download the source code make sure to clone the repository, don't just download `Main.java` as it also needs the `dataset` and `earth.jpg` files in the locations they are.

## Controls
- <kbd>A</kbd> - Moves Earth LEFT.
- <kbd>D</kbd> - Moves Earth RIGHT.
- <kbd>W</kbd> - Moves Earth UP.
- <kbd>S</kbd> - Moves Earth DOWN.

- <kbd>←</kbd>, <kbd>→</kbd> - Rotate Earth around the vertical axis.
- <kbd>↑</kbd>, <kbd>↓</kbd> - Rotate Earth around the horizontal axis.

- <kbd>+</kbd>, <kbd>-</kbd> - Zoom in and out.

## Screenshot
![Screenshot](/screenshots/screenshot.png?raw=true "Screenshot of SatGraph")
