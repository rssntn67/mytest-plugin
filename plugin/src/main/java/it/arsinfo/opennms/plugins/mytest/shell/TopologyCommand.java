package it.arsinfo.opennms.plugins.mytest.shell;

import it.arsinfo.opennms.plugins.mytest.TopologyForwarder;
import it.arsinfo.opennms.plugins.mytest.model.Topology;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.table.Col;
import org.apache.karaf.shell.support.table.ShellTable;
import org.opennms.integration.api.v1.model.TopologyProtocol;

@Command(scope = "opennms-mytestplugin", name = "topology", description = "get the topology for specified protocol")
@Service
public class TopologyCommand implements Action {
    @Reference
    private Session session;

    @Reference
    private TopologyForwarder forwarder;

    @Argument(name = "protocol", description = "one of supported protocols", required = true)
    public String protocol = null;

    @Override
    public Object execute() {
        TopologyProtocol topologyProtocol;
        switch (protocol) {
            case "lldp":
                topologyProtocol = TopologyProtocol.LLDP;
                break;
            case "cdp":
                topologyProtocol = TopologyProtocol.CDP;
                break;
            case "bridge":
                topologyProtocol = TopologyProtocol.BRIDGE;
                break;
            case "ospf":
                topologyProtocol = TopologyProtocol.OSPF;
                break;
            case "isis":
                topologyProtocol = TopologyProtocol.ISIS;
                break;
            case "ospfarea":
                topologyProtocol = TopologyProtocol.OSPFAREA;
                break;
            case "networkrouter":
                topologyProtocol = TopologyProtocol.NETWORKROUTER;
                break;
            case "nodes":
                topologyProtocol = TopologyProtocol.NODES;
                break;
            case "all":
                topologyProtocol = TopologyProtocol.ALL;
                break;
            case "user":
                topologyProtocol = TopologyProtocol.USERDEFINED;
                break;
            default:
                System.out.println(protocol +" not supported");
                return null;
        }
        Topology topology = forwarder.forwardTopologies(topologyProtocol);
        System.out.println("Topology for: " + topology.getId());
        final var table = new ShellTable()
                .size(session.getTerminal().getWidth() - 1)
                .column(new Col("id").maxSize(72))
                .column(new Col("source").maxSize(72))
                .column(new Col("target").maxSize(72));

        topology.getLinks().forEach(link -> {
            final var row = table.addRow();
            row.addContent(link.getId());
            row.addContent(link.getSource());
            row.addContent(link.getTarget());
        });

        table.print(System.out, true);
        return null;
    }
}
