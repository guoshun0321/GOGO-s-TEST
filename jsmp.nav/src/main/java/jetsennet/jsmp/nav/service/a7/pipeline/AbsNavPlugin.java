package jetsennet.jsmp.nav.service.a7.pipeline;

public abstract class AbsNavPlugin implements INavPlugin
{

	private INavPlugin next;

	private INavPlugin pre;

	protected NavPipelineContext context;

	public AbsNavPlugin()
	{
	}

	@Override
	public void setContext(NavPipelineContext context)
	{
		this.context = context;
	}

	@Override
	public INavPlugin addNext(INavPlugin next)
	{
		this.next = next;
		return next;
	}

	@Override
	public INavPlugin addPre(INavPlugin pre)
	{
		this.pre = pre;
		return pre;
	}

	@Override
	public INavPlugin next()
	{
		return next;
	}

	@Override
	public INavPlugin pre()
	{
		return pre;
	}

}
