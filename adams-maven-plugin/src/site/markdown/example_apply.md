# Example: apply

The following example will turn the flow `src/main/flows/my_cool_method.flow`
into a Java class called `my.flows.MyCoolMethod` with an `apply` method
for executing the flow (depending in the type of top-level actor, this will
be either a procedure or a function):


    <project>
    ...
    <build>
      <plugins>
        <plugin>
          <groupId>${project.groupId}</groupId>
          <artifactId>${project.artifactId}</artifactId>
          <version>${project.version}</version>
          <executions>
            <execution>
              <id>apply</id>
              <goals>
                <goal>apply</goal>
              </goals>
            </execution>
          </executions>
          <configuration>
            <flow>src/main/flows/my_cool_method.flow</flow>
            <packageName>my.flows</packageName>
            <simpleName>MyCoolMethod</simpleName>
          </configuration>
        </plugin>
        ...
      </plugins>
    </build>
    ...
    </project>

