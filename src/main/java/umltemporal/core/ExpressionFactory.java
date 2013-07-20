/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umltemporal.core;

/**
 *
 * @author szymzet
 */
public class ExpressionFactory {

		public ExpressionFactory() {
		}

		public Expression get(ExpressionType type) {

				switch (type) {
						case BRANCHING:
								return new BranchingExpression();
						case CONCURRENCY:
								return new ConcurrencyExpression();
						case LOOP:
								return new LoopExpression();
						case SEQUENCE:
								return new SequenceExpression();
						default:
								throw new IllegalArgumentException("Not supported ExpressionType");
				}
		}

		public Expression get(String name) {
				return new ActionExpression(name);
		}
}
