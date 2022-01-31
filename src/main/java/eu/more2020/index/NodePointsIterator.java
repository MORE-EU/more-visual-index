package eu.more2020.index;

import eu.more2020.index.query.QueryNode;



public class NodePointsIterator extends AbstractPointIterator {
    QueryNode queryNode;
    private int i = -1;
//    private static final Logger LOG = LogManager.getLogger(NodePointsIterator.class);

    public NodePointsIterator(QueryNode queryNode) {
        this.queryNode = queryNode;
    }

    protected Point getNext() {
//        ContainmentExaminer containmentExaminer = queryNode.getContainmentExaminer();
//        TreeNode node = queryNode.getNode();
//        try {
//            if (containmentExaminer == null) {
//                if (node.getPoints() == null){
//                    LOG.error(queryNode);
//                    LOG.error(queryNode.getTile().getCategoricalColumns());
//                }
//                return node.getPoints().get(++i);
//            }
//            Point point;
//            while (!containmentExaminer.contains(point = node.getPoints().get(++i))) ;
//            return point;
//        } catch (IndexOutOfBoundsException e) {
//            return null;
//        }
        return null;
    }

    public QueryNode getQueryNode() {
        return queryNode;
    }
}