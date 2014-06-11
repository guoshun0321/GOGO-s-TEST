package jetsennet.jsmp.nav.service.a7;

import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmBaseKey;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmFileItem;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmFileItems;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmPicture;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmPictures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jsmp.nav.cache.xmem.MemcachedOp;
import jetsennet.jsmp.nav.entity.PgmBaseEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntityUtil;

public class NavBusinessUtil
{

    private static final MemcachedOp cache = MemcachedOp.getInstance();

    public static final ResponseEntity getItemInfo(ProgramEntity prog)
    {
        ResponseEntity resp = ResponseEntityUtil.obj2Resp(prog, "Selectableltem", null);
        PgmBaseEntity pgmBase = cache.get(pgmBaseKey(prog.getPgmId()));
        ResponseEntityUtil.obj2Resp(pgmBase, null, resp);

        List<String> picIds = cache.getListString(pgmPictures(prog.getPgmId()));
        List<String> picKeys = new ArrayList<>(picIds.size());
        for (String picId : picIds)
        {
            picKeys.add(pgmPicture(picId));
        }
        Map<String, Object> picMap = cache.gets(picKeys);
        Set<String> keys = picMap.keySet();
        for (String key : keys)
        {
            Object obj = picMap.get(key);
            if (obj != null)
            {
                resp.addChild(ResponseEntityUtil.obj2Resp(obj, "Image", null));
            }
        }

        List<String> itemIds = cache.getListString(pgmFileItems(prog.getPgmId()));
        List<String> itemKeys = new ArrayList<>(itemIds.size());
        for (String itemId : itemIds)
        {
            picKeys.add(pgmFileItem(itemId));
        }
        Map<String, Object> itemMap = cache.gets(itemKeys);
        keys = itemMap.keySet();
        for (String key : keys)
        {
            Object obj = itemMap.get(key);
            if (obj != null)
            {
                resp.addChild(ResponseEntityUtil.obj2Resp(obj, "SelectionChoice", null));
            }
        }
        return resp;
    }

}
