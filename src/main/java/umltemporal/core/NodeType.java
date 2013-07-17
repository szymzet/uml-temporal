package umltemporal.core;

/**
 * Enum representing _supported_ node types in UML activity diagrams which are
 * imported.
 */
public enum NodeType {
	ACTIVITY_FINAL_NODE("uml:ActivityFinalNode"), DECISION_NODE(
			"uml:DecisionNode"), FORK_NODE("uml:ForkNode"), INITIAL_NODE(
			"uml:InitialNode"), JOIN_NODE("uml:JoinNode"), MERGE_NODE(
			"uml:MergeNode"), OPAQUE_ACTION("uml:OpaqueAction");

	private String name;

	private NodeType(String s) {
		name = s;
	}

	/**
	 * Create appropriate NodeType from xmi:type attribute value.
	 * 
	 * @param xmiType
	 * @return
	 */
	public static NodeType fromString(String xmiType) {
		if (xmiType == null) {
			throw new IllegalArgumentException(
					"Trying to create a node type from null string");
		}

		for (NodeType type : NodeType.values()) {
			if (xmiType.equals(type.getXmiTypeValue())) {
				return type;
			}
		}

		throw new IllegalArgumentException("No constant with text \"" + xmiType
				+ "\" found");
	}

	/**
	 * Return the associated string.
	 * 
	 * @return
	 */
	public String getXmiTypeValue() {
		return name;
	}
}
