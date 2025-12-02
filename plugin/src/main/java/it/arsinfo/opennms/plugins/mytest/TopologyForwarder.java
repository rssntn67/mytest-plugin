package it.arsinfo.opennms.plugins.mytest;

import it.arsinfo.opennms.plugins.mytest.model.Link;
import it.arsinfo.opennms.plugins.mytest.model.Topology;
import kotlin.Pair;
import org.opennms.integration.api.v1.dao.EdgeDao;
import org.opennms.integration.api.v1.model.Node;
import org.opennms.integration.api.v1.model.TopologyEdge;
import org.opennms.integration.api.v1.model.TopologyPort;
import org.opennms.integration.api.v1.model.TopologyProtocol;
import org.opennms.integration.api.v1.model.TopologySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TopologyForwarder {
    private static final Logger LOG = LoggerFactory.getLogger(TopologyForwarder.class);

    private final EdgeDao edgeDao;

    public TopologyForwarder(EdgeDao edgeDao) {
        this.edgeDao = Objects.requireNonNull(edgeDao);
    }

    public Set<TopologyProtocol> getProtocols() {
        return new HashSet<>(edgeDao.getProtocols());
    }

    protected static Pair<String, String> getFromId(String id) {
        List<String> values = Arrays.stream(id.split("\\|")).toList();
        String source = values.get(0);
        String target = values.get(1);
        return new Pair<>(source, target);
    }

    private static class EndPointVisitor implements TopologyEdge.EndpointVisitor {
        final private String id;
        final private Link link;

        private EndPointVisitor(String id, Link link) {
            this.id = id;
            this.link = link;
        }

        private String toLinkId(Node node) {
            return "node-" + node.getId();
        }

        private String toLinkId(TopologyPort port) {
            return "node-" + port.getNodeCriteria() + "port-" + port.getIfName();
        }

        private String toLinkId(TopologySegment segment) {
            return "segment-" + segment.getSegmentCriteria();
        }

        @Override
        public void visitSource(Node node) {
            link.setSource(toLinkId(node));
        }

        @Override
        public void visitSource(TopologyPort port) {
            link.setSource(toLinkId(port));
        }

        @Override
        public void visitSource(TopologySegment segment) {
            link.setSource(toLinkId(segment));
        }

        @Override
        public void visitTarget(Node node) {
            link.setTarget(toLinkId(node));
        }

        @Override
        public void visitTarget(TopologyPort port) {
            link.setTarget(toLinkId(port));
        }

        @Override
        public void visitTarget(TopologySegment segment) {
            Pair<String,String> pair = getFromId(this.id);
            link.setSource(pair.getFirst());
            link.setTarget(pair.getSecond()+"::"+toLinkId(segment));
        }

        public Link getLink() {
            return this.link;
        }
    }

    public Topology forwardTopologies(TopologyProtocol protocol) {
            List<Link> links = new LinkedList<>();
            for (TopologyEdge edge : edgeDao.getEdges(protocol)) {
                EndPointVisitor visitor = new EndPointVisitor(edge.getId(), new Link(edge.getId()));
                edge.visitEndpoints(visitor);
                links.add(visitor.getLink());
            }
            LOG.info("Forwarding {} links for topology protocol: {}", links.size(), protocol);
            return new Topology(protocol.name(), links);
    }
}
