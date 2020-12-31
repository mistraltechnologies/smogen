# SMOG Matcher Generator

![Build](https://github.com/richardjwilson/smogen/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

<!-- Plugin description -->
Generates a companion matcher class for a selected Java class based on the SMOG library extension to Hamcrest.

The SMOG library supports property matching on object graphs. For more details, see the project page
at <a href="https://github.com/mistraltechnologies/smog">GitHub</a>

This plugin is also hosted at <a href="https://github.com/mistraltechnologies/smogen">GitHub</a>

## Usage

This plugin adds a Generate Matcher menu option to the Code menu in IntelliJ IDEA.

It generates matcher classes that can be used as Hamcrest matchers.

The generated class has a dependency on the supporting SMOG library, hosted at https://github.com/mistraltechnologies/smog
and available from <a href="http://search.maven.org/#artifactdetails|com.mistraltech.smog|smog-core|1.0|jar)">Maven Central</a>.

The menu option is enabled when a Java class is selected.

After selecting the menu option, a dialog is provided to specify parameters relating to how the new matcher will be generated.

Confirming the dialog causes the new class to be generated.
If the class already exists, you are given the option to overwrite the existing class or abort.

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "smogen"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/richardjwilson/smogen/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Verify the [pluginGroup](/gradle.properties), [plugin ID](/src/main/resources/META-INF/plugin.xml) and [sources package](/src/main/kotlin).
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html).
- [ ] [Publish a plugin manually](https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/publishing_plugin.html) for the first time.
- [ ] Set the Plugin ID in the above README badges.
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html).
- [x] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
