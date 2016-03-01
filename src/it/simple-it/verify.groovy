File appFile = new File( basedir, "target/app.properties" );

assert appFile.isFile()

assert appFile.text.contains("x=catch-and-pass-as-project-property")

