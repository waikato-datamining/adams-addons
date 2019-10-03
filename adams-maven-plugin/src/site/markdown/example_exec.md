# Example: exec

The following example will turn the flow `src/main/flows/my_cool_flow.flow`
into a Java class called `my.flows.MyCoolFlow`:


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
              <id>exec</id>
              <goals>
                <goal>exec</goal>
              </goals>
            </execution>
          </executions>
          <configuration>
            <flow>src/main/flows/my_cool_flow.flow</flow>
            <packageName>my.flows</packageName>
            <simpleName>MyCoolFlow</simpleName>
          </configuration>
        </plugin>
        ...
      </plugins>
    </build>
    ...
    </project>

