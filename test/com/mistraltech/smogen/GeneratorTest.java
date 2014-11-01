package com.mistraltech.smogen;

public class GeneratorTest extends AbstractGeneratorTest {

  public void testMinimalClass() {
    doTest("minimal_class", generatorProperties());
  }

  public void testSimpleProperty() {
    doTest("simple_property", generatorProperties());
  }
}
