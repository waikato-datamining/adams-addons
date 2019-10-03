# Example: multi

It is, of course, possible to specify multiple executions. In that case,
the `<configuration>` tag must be moved inside the `<execution>` flag:


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
              <configuration>
                <flow>src/main/flows/my_cool_flow.flow</flow>
                <packageName>my.flows</packageName>
                <simpleName>MyCoolFlow</simpleName>
              </configuration>
            </execution>
            <execution>
              <id>apply</id>
              <goals>
                <goal>apply</goal>
              </goals>
              <configuration>
                <flow>src/main/flows/my_cool_method.flow</flow>
                <packageName>my.flows</packageName>
                <simpleName>MyCoolMethod</simpleName>
              </configuration>
            </execution>
          </executions>
        </plugin>
        ...
      </plugins>
    </build>
    ...
    </project>

