<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# SMOG Matcher Generator Changelog

## [Unreleased]
### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security
## [1.0.2]
### Fixed
- Replaced default icon with custom icon

## [1.0.0]
### Added
- Plugin recreated using [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Refreshed plugin code base

### Fixed
- Issue where generated extensible matcher class attempts to access private member variable 
- Redundant generic type specifier in generated property matcher initializer

### Removed
- Option for generating interface without factory methods (supporting pre- Java 8)

## 0.5.0
### Added

- Support for generating interface-only matchers. Interface-only matchers can be used with the SMOG-Javassist
  library, which generates the matcher implementation at runtime, significantly cutting down on the amount of
  boiler-plate code maintained within a project. 
  SMOG-Javassist is hosted at <a href="https://github.com/mistraltechnologies/smog-javassist">GitHub</a>.

## 0.4.0
### Fixed
- Incompatibility with IDEA 14.0

## 0.3.0
### Fixed
- Correction to the 'since-build' metadata. Plugin requires IntelliJ IDEA version 13.1 or later.

## 0.2.0

This version is compatible with smog-core library version 1.0 available from maven central.

### Added
- New code generator manages imports better and avoids the need for
  unnecessary fully qualified names in the code.
- Matchers generated for abstract classes are no longer abstract, allowing them to be instantiated and used
  for matching any concrete subclass of the matched class.
- The generator now extensively supports generic properties and generic classes.
- Generated matchers get reformatted to use project formatting settings.
- Generated matcher classes are now annotated with @Matches.

## 0.1.0

First release.
