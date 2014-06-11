package jetsennet.jsmp.nav.syn.cache;

import static jetsennet.jsmp.nav.syn.CachedKeyUtil.columnKey;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.subColumn;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.topColumn;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.syn.AbsUpdateMediaInfoCache;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheColumn extends AbsUpdateMediaInfoCache<ColumnEntity>
{

    public void insert(ColumnEntity obj)
    {
        String key = columnKey(obj.getColumnId());
        cache.put(key, obj);
        cache.put(CachedKeyUtil.columnAssetKey(obj.getAssetId()), obj.getColumnId());

        if (obj.getParentId() == 0)
        {
            // 顶级栏目
            this.add2CachedSet(topColumn(), obj.getColumnId());
        }
        else
        {
            // 子栏目
            String cachedKey = subColumn(obj.getParentId(), obj.getRegionCode());
            this.add2CachedSet(cachedKey, obj.getColumnId());
        }
    }

    public void update(ColumnEntity obj)
    {
        String key = columnKey(obj.getColumnId());
        this.cache.put(key, obj);
    }

    public void delete(ColumnEntity obj)
    {
        if (obj.getParentId() == 0)
        {
            // 顶级栏目
            this.del2CachedSet(topColumn(), obj.getColumnId());
        }
        else
        {
            // 删除和父栏目的关系
            String cachedKey = subColumn(obj.getParentId(), obj.getRegionCode());
            this.del2CachedSet(cachedKey, obj.getColumnId());
        }
        // 删除和子栏目的关系
        String cachedKey = subColumn(obj.getColumnId(), obj.getRegionCode());
        this.cache.del(cachedKey);
        // 删除栏目信息
        this.cache.del(CachedKeyUtil.columnAssetKey(obj.getAssetId()));
        this.cache.del(columnKey(obj.getColumnId()));
    }

}
