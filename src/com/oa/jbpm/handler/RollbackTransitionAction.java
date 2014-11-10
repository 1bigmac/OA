package com.oa.jbpm.handler;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.EndState;

public class RollbackTransitionAction implements ActionHandler {

	private Log logger = LogFactory.getLog(RollbackTransitionAction.class);
	
	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("����Action");
		
		//�����ǰ�ڵ����ڽ����ڵ㣬�����贴���κη���Transition
		if(executionContext.getNode() instanceof EndState){
			return;
		}
		
		boolean ignore = false;
		
		//�õ���ǰָ��Ľڵ����п���ʹ�õ�transition�����б�
		Set ts = executionContext.getToken().getAvailableTransitions();
		for (Iterator iterator = ts.iterator(); iterator.hasNext();) {
			Transition t = (Transition) iterator.next();
			if(t.getName().equals("����")){
				ignore = true;
				break;
			}
		}
		
		//�����δ��������Transition���󣬾Ϳ��Լ�������
		if(!ignore){
			String preNodeName = (String)executionContext.getContextInstance().getVariable("preNodeName");
			if(preNodeName != null){ //ǰһ���ڵ�ǿգ���Ҫ��������Transition
				
				//�ӵ�ǰ�ڵ�
				Node from = executionContext.getNode();
				
				//����һ���ڵ���Ϊ�յ�
				Node to = executionContext.getProcessDefinition().getNode(preNodeName);
				
				//��������Transition����
				Transition transition = new Transition();
				transition.setName("����");
				from.addLeavingTransition(transition);
				to.addArrivingTransition(transition);
			}
			executionContext.getContextInstance().setVariable("preNodeName", executionContext.getNode().getName());
		}
		
	}

}
