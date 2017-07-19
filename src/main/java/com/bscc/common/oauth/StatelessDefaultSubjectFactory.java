package com.bscc.common.oauth;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  通过调用context.setSessionCreationEnabled(false)表示不创建会话；如果之后调用
 Subject.getSession()将抛出DisabledSessionException异常。
 * @author Kent
 * @since 2017-06-23 18:25
 */
public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {
    private final static Logger logger = LoggerFactory.getLogger(StatelessDefaultSubjectFactory.class);
    @Override
    public Subject createSubject(SubjectContext context) {
        //不创建session.
        context.setSessionCreationEnabled(false);
        logger.debug("shiro.config.subjectFactory.createSubject.SessionCreationEnabled.false");
        return super.createSubject(context);
    }
}