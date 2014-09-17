package jetsennet.jsmp.nav.service.a7;

import java.util.List;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;

public interface INavData
{

	/** 
	 * 获取顶级栏目
	 * 
	 * @return
	 */
	public abstract List<ColumnEntity> getTopColumns();

	/**
	 * 获取所有的子栏目
	 * 
	 * @param column
	 * @return
	 */
	public abstract List<ColumnEntity> subColumns(ColumnEntity column);

	/**
	 * 获取栏目对应的图片
	 * 
	 * @param column
	 * @return
	 */
	public abstract List<PictureEntity> columnPicturs(ColumnEntity column);

	/**
	 * 根据assetId获取栏目信息
	 * 
	 * @param assetId
	 * @return
	 */
	public abstract ColumnEntity getColumnByAssetId(String assetId);

	/**
	 * 获取栏目下的节目AssetId
	 * 
	 * @param columnId
	 * @return
	 */
	public abstract List<String> columnProgramIds(String assetId);

	/**
	 * 根据assetId获取节目信息
	 * 
	 * @param assetId
	 * @return
	 */
	public abstract ProgramEntity getProgramByAssetId(String assetId);

	/**
	 * 获取所有节目信息
	 * 
	 * @param pgmAssetIds
	 * @return
	 */
	public abstract List<ProgramEntity> getPrograms(List<String> pgmAssetIds);

	/**
	 * 获取子节目
	 * @param pgmId
	 * @return
	 */
	public abstract List<ProgramEntity> getSubPrograms(String pAssetId);

	/**
	 * 获取节目基本信息
	 * @param pgmId
	 * @return
	 */
	public abstract Object getPgmBase(int pgmId);

	/**
	 * 获取节目的图片
	 * @param pgmId
	 * @return
	 */
	public abstract List<FileItemEntity> getPgmPictures(int pgmId);

	/**
	 * 获取节目的文件
	 * 
	 * @param pgmId
	 * @return
	 */
	public abstract List<FileItemEntity> getPgmItems(int pgmId);

	/**
	 * 获取创作者
	 * 
	 * @param pgmId
	 * @return
	 */
	public abstract List<CreatorEntity> getCreators(int pgmId);

	/**
	 * 根据assetId获取FileItem
	 * 
	 * @param assetId
	 * @return
	 */
	public abstract FileItemEntity getFileItemByAssetId(String assetId);

	/**
	 * 根据assetId获取PlayBillItem
	 * 
	 * @param assetId
	 * @return
	 */
	public abstract PlaybillItemEntity getPlayBillItemByAssetId(String assetId);

	/**
	 * 添加用于和SM系统通讯的TOKEN
	 * 
	 * @param playUrl
	 */
	public abstract String addSMKey(String playUrl);

	/**
	 * 获取指定区域和语言的ChannelId
	 * 
	 * @param region
	 * @param lang
	 * @return
	 */
	public abstract List<String> getChannelIds(String region, String lang);

	/**
	 * 获取频道列表
	 * 
	 * @param chIds
	 * @return
	 */
	public abstract List<ChannelEntity> getChannels(List<String> chAssetIds);

	/**
	 * 获取物理频道
	 * 
	 * @param chlId
	 * @return
	 */
	public abstract List<PhysicalChannelEntity> getPhysicalChannels(String chlAssetId);

	/**
	 * 获取PlayBillItem列表
	 * 
	 * @param chlAssetId 频道ID
	 * @param day 日期
	 * @return
	 */
	public abstract List<String> getPlayBillItemIds(String chlAssetId, long day);

}
