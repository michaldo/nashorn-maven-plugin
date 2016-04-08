# nashorn-maven-plugin

### Purpose

`nashorn-maven-plugin` allows execute little piece of Javascript code during build. It is lightweight 
alternative to implement regular plugin

### Example

Let say you want touch file 'x' during build. Classic Ant way:
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-antrun-plugin</artifactId>
      <version>1.6</version>
      <executions>
        <execution>
          <phase>process-resources</phase>
            <configuration>
              <target>
                <touch file="${basedir}/x" />
              </target>
            </configuration>
            <goals><goal>run</goal></goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

Nashorn  way:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>io.github.michaldo</groupId>
      <artifactId>nashorn-maven-plugin</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <executions>
        <execution>
          <phase>process-resources</phase>
          <goals><goal>eval</goal></goals>
          <configuration>
            <script>
              var path = java.nio.file.Paths.get($basedir, 'x');
              var Files = Java.type('java.nio.file.Files');
              if (!Files.exists(path)) {
                Files.createFile(path);
              } else {
                var FileTime = Java.type('java.nio.file.attribute.FileTime');
                Files.setLastModifiedTime(path, FileTime.from(java.time.Instant.now()))
              }
            </script>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

No benefit of Nashorn way. But let say you have multi-module project with many jar and war module and you want to touch 'x' 
only for war module. For Ant it's impossible, huh? For Nashorn it is pretty easy

```xml
<build>
  <plugins>
    <plugin>
      <groupId>io.github.michaldo</groupId>
      <artifactId>nashorn-maven-plugin</artifactId>
      <version>0.0.1</version>
      <executions>
        <execution>
          <phase>process-resources</phase>
          <goals><goal>eval</goal></goals>
          <configuration>
            <script>
              
              if ($project.packaging != 'war') return;
              
              var path = java.nio.file.Paths.get($basedir, 'x');
              var Files = Java.type('java.nio.file.Files');
              if (!Files.exists(path)) {
                Files.createFile(path);
              } else {
                var FileTime = Java.type('java.nio.file.attribute.FileTime');
                Files.setLastModifiedTime(path, FileTime.from(java.time.Instant.now()))
              }
            </script>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

### Usage
```xml
<plugin>
  <groupId>io.github.michaldo</groupId>
  <artifactId>nashorn-maven-plugin</artifactId>
  <version>0.0.1</version>
  <executions>
    <execution>
      <phase>initialize</phase>
      <goals><goal>eval</goal></goals>
        <configuration>
        <script>
          print('hello world');
        </script>
      </configuration>
    </execution>
  </executions>
</plugin>
```

### Goals
`nashorn:eval`: executes javascript code
##### Parameters
`script` : required script code

### Maven API
The following Maven objects are injected in Javascript space

| Variable | Class
|--- | --- 
|$project| actual [MavenProject](https://maven.apache.org/ref/3.1.1/maven-core/apidocs/org/apache/maven/project/MavenProject.html)
|$session| [MavenSession](https://maven.apache.org/ref/3.1.1/maven-core/apidocs/org/apache/maven/execution/MavenSession.html)
|$mojo|[MojoExecution](https://maven.apache.org/ref/3.1.1/maven-core/apidocs/org/apache/maven/plugin/MojoExecution.html)
|$plugin|[MojoExecution.getMojoDescriptor().getPluginDescriptor()](http://maven.apache.org/ref/3.1.1/maven-plugin-api/apidocs/org/apache/maven/plugin/descriptor/MojoDescriptor.html)
|$settings|[MavenSession.getSettings()](http://maven.apache.org/ref/3.1.1/maven-settings/apidocs/org/apache/maven/settings/Settings.html)
|$localRepository|[MavenSession.getLocalRepository()()](http://maven.apache.org/ref/3.1.1/maven-artifact/apidocs/org/apache/maven/artifact/repository/ArtifactRepository.html)
|$reactorProjects|[MavenSession.getProjects()](https://maven.apache.org/ref/3.1.1/maven-core/apidocs/org/apache/maven/execution/MavenSession.html#getProjects%28%29)
|$repositorySystemSession|[MavenSession.getRepositorySession()](https://maven.apache.org/ref/3.1.1/maven-core/apidocs/org/apache/maven/execution/MavenSession.html#getRepositorySession%28%29)
|$executedProject|[MavenProject.getExecutionProject()](https://maven.apache.org/ref/3.1.1/maven-core/apidocs/org/apache/maven/project/MavenProject.html#getExecutionProject%28%29)
|$basedir|[MavenProject.getBasedir()](https://maven.apache.org/ref/3.1.1/maven-core/apidocs/org/apache/maven/project/MavenProject.html#getBasedir%28%29)

### Requirements

Maven 3.1

Java 8
