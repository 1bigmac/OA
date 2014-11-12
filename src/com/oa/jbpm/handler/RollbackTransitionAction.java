package com.oa.jbpm.handler;

import java.util.Iterator;
import java.util.Set;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.TaskNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oa.util.Constant;

public class RollbackTransitionAction implements ActionHandler {
	
	private Logger logger = LoggerFactory.getLogger(RollbackTransitionAction.class);
	
	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("����Action");
		
		String preNodeName = (String)executionContext.getContextInstance().getVariable("preNodeName");
		
		logger.info(executionContext.getToken().getId()+"-------------getToken().getId()--------------------------");
		
		logger.info((executionContext.getToken().getParent())==null ? "���ڵ�Ϊ��" : executionContext.getToken().getParent().getId()+"==================----------executionContext.getToken().getParent().getId()=====================");
		logger.info(executionContext.getToken()+"===");
		logger.info(executionContext.getProcessInstance().getRootToken().getId()+"--------------getProcessInstance().getRootToken()-------------------------");
		//�����ǰ�ڵ����ڽ����ڵ㣬�����贴���κη���Transition
		if(executionContext.getNode() instanceof EndState){
			return;
		}
		
		TaskNode taskNode=(TaskNode) executionContext.getNode();
		Set<Transition> arrayingTransition=taskNode.getArrivingTransitions();
		//��ֹ���������Ϊһ��֩����
		if(arrayingTransition.size()<2){
			boolean ignore = false;
			
			//�õ���ǰָ��Ľڵ����п���ʹ�õ�transition�����б�
			Set ts = executionContext.getToken().getAvailableTransitions();
			
			for (Iterator iterator = ts.iterator(); iterator.hasNext();) {
				Transition t = (Transition) iterator.next();
				String  transitionName=t.getName();
				if(transitionName.matches(Constant.rollbackRegex)){
					ignore = true;
					break;
				}
				
				/*
				if(t.getName().equals("����")){
					ignore = true;
					break;
				}*/
			}
			
			//�����δ��������Transition���󣬾Ϳ��Լ�������
			if(!ignore){
				
				if(preNodeName != null){ //ǰһ���ڵ�ǿգ���Ҫ��������Transition
					
					//�ӵ�ǰ�ڵ�
					Node from = executionContext.getNode();
					
					//����һ���ڵ���Ϊ�յ�
					Node to = executionContext.getProcessDefinition().getNode(preNodeName);
					
					//��������Transition����
					Transition transition = new Transition();
					transition.setName(Constant.rollback+preNodeName);
					from.addLeavingTransition(transition);
					to.addArrivingTransition(transition);
				}
				executionContext.getContextInstance().setVariable("preNodeName", executionContext.getNode().getName());
			}
			
		}
	
	}

}
