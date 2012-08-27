/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.client.shell;

import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.sqoop.client.core.Environment;
import org.apache.sqoop.client.request.ConnectorRequest;
import org.apache.sqoop.json.ConnectorBean;
import org.apache.sqoop.model.MConnector;
import org.apache.sqoop.model.MJobForms;
import org.codehaus.groovy.tools.shell.IO;

import static org.apache.sqoop.client.display.FormDisplayer.*;

@SuppressWarnings("serial")
public class ShowConnectorFunction extends SqoopFunction
{
  public static final String ALL = "all";
  public static final String CID = "cid";

  private IO io;
  private ConnectorRequest conntectorRequest;

  @SuppressWarnings("static-access")
  protected ShowConnectorFunction(IO io) {
    this.io = io;

    this.addOption(OptionBuilder
        .withDescription("Display all connectors")
        .withLongOpt(ALL)
        .create(ALL.charAt(0)));
    this.addOption(OptionBuilder.hasArg().withArgName("cid")
        .withDescription(  "Display the connector with cid" )
        .withLongOpt(CID)
        .create(CID.charAt(0)));
  }

  public void printHelp(PrintWriter out) {
    out.println("Usage: show connector");
    super.printHelp(out);
  }

  public Object execute(List<String> args) {
    if (args.size() == 1) {
      printHelp(io.out);
      io.out.println();
      return null;
    }

    CommandLine line = parseOptions(this, 1, args);
    if (line.hasOption(ALL)) {
      showConnector(null);

    } else if (line.hasOption(CID)) {
      showConnector(line.getOptionValue(CID));
    }

    return null;
  }

  private void showConnector(String cid) {
    if (conntectorRequest == null) {
      conntectorRequest = new ConnectorRequest();
    }
    ConnectorBean connectorBean =
      conntectorRequest.doGet(Environment.getServerUrl(), cid);
    MConnector[] connectors = connectorBean.getConnectors();

    io.out.println("@|bold " + connectors.length + " connector(s) to show: |@");
    for (int i = 0; i < connectors.length; i++) {
      MConnector connector = connectors[i];

      io.out.print("Connector with id ");
      io.out.print(connector.getPersistenceId());
      io.out.println(":");

      io.out.print("  Name: ");
      io.out.println(connector.getUniqueName());
      io.out.print("  Class: ");
      io.out.println(connector.getClassName());

      displayFormDetails(io, connector);

    }

    io.out.println();
  }
}