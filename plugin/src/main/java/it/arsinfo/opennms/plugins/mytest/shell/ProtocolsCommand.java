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

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Command(scope = "opennms-mytestplugin", name = "protocols", description = "get the protocols")
@Service
public class ProtocolsCommand implements Action {

    @Reference
    private Session session;

    @Reference
    private TopologyForwarder forwarder;

    @Override
    public Object execute() {
        final var table = new ShellTable()
                .size(session.getTerminal().getWidth() - 1)
                .column(new Col("protocol").maxSize(72));
        forwarder.getProtocols().forEach(p -> {
            final var row = table.addRow();
            row.addContent(p);
        });

        table.print(System.out, true);
        return null;
    }
}
