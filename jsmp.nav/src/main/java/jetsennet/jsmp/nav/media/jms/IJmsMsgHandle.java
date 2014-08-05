package jetsennet.jsmp.nav.media.jms;


public interface IJmsMsgHandle
{

	public void handleModify(DataSynContentEntity content) throws Exception;

	public void handleDelete(DataSynContentEntity content) throws Exception;

	public void handleAll(DataSynContentEntity content) throws Exception;

}
