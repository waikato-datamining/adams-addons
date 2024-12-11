# What is the ADAMS Maven Plugin?

This plugin provides some ADAMS-specific support, like code generation
from workflows.

## Goals Overview

The adams-maven-plugin has the following goals:

* [adams:exec](./exec-mojo.html) Generates Java code from a flow, with the 
  classes to be executed via their main method.
* [adams:apply](./apply-mojo.html) Generates Java code from a flow, with the 
  flows to be executed as methods (via the `apply` method).

## Usage examples

A set of usage examples are found within the following pages:

<table>
    <tr>
        <th width="35%">Example page</th>
        <th width="60%">Description</th>
    </tr>
    <tr>
        <td><a href="./example_exec.html">Example: exec</a></td>
        <td><a href="./example_apply.html">Example: apply</a></td>
        <td><a href="./example_multi.html">Example: multi</a></td>
    </tr>
</table>
