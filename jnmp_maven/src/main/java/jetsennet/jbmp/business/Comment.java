package jetsennet.jbmp.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.CommentEntity;
import jetsennet.jbmp.entity.KnowledgeEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.util.SerializerUtil;

/**
 * @author ?
 */
public class Comment
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addComment(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "BMP_KNOWLEDGECOMMENT");

        // 评论所属知识库文章的更新时间设为当前时间，评论数加1
        DefaultDal<KnowledgeEntity> dalKnowledge = new DefaultDal<KnowledgeEntity>(KnowledgeEntity.class);
        KnowledgeEntity knowledge = dalKnowledge.get(Integer.parseInt(model.get("KNOWLEDGE_ID")));
        if (knowledge != null)
        {
            knowledge.setUpdateTime(new Date());
            knowledge.setCommentCount(knowledge.getCommentCount() + 1);
            dalKnowledge.update(knowledge);
        }

        DefaultDal<CommentEntity> dal = new DefaultDal<CommentEntity>(CommentEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateComment(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "BMP_KNOWLEDGECOMMENT");

        // 评论所属知识库文章的更新时间设为当前时间
        DefaultDal<KnowledgeEntity> dalKnowledge = new DefaultDal<KnowledgeEntity>(KnowledgeEntity.class);
        KnowledgeEntity knowledge = dalKnowledge.get(Integer.parseInt(model.get("KNOWLEDGE_ID")));
        if (knowledge != null)
        {
            knowledge.setUpdateTime(new Date());
            dalKnowledge.update(knowledge);
        }

        DefaultDal<CommentEntity> dal = new DefaultDal<CommentEntity>(CommentEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int deleteComment(int keyId) throws Exception
    {
        DefaultDal<CommentEntity> dal = new DefaultDal<CommentEntity>(CommentEntity.class);
        CommentEntity comment = dal.get(keyId);

        // 评论所属知识库文章的评论数减1
        DefaultDal<KnowledgeEntity> dalKnowledge = new DefaultDal<KnowledgeEntity>(KnowledgeEntity.class);
        KnowledgeEntity knowledge = dalKnowledge.get(comment.getKnowledgeId());
        if (knowledge != null)
        {
            knowledge.setCommentCount(knowledge.getCommentCount() - 1);
            dalKnowledge.update(knowledge);
        }

        return dal.delete(keyId);
    }

    /**
     * 根据条件删除
     * @param condition 条件
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int deleteCommentByCondition(SqlCondition... condition) throws Exception
    {
        DefaultDal<CommentEntity> dal = new DefaultDal<CommentEntity>(CommentEntity.class);
        List<CommentEntity> comments = dal.getLst(condition);

        // 评论所属知识库文章的评论数减少
        if (comments.size() > 0)
        {
            DefaultDal<KnowledgeEntity> dalKnowledge = new DefaultDal<KnowledgeEntity>(KnowledgeEntity.class);
            KnowledgeEntity knowledge = dalKnowledge.get(comments.get(0).getKnowledgeId());
            knowledge.setCommentCount(knowledge.getCommentCount() - comments.size());
            dalKnowledge.update(knowledge);
        }

        return dal.delete(condition);
    }
}
