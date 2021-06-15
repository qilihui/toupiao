package com.example.demo.service;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.demo.dao.OpenIdDao;
import com.example.demo.entity.OpenId;
import com.example.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * @author qilihui
 * @date 2021/6/15 20:19
 */
@Service
@Slf4j
public class ToupiaoService {
    @Autowired
    private OpenIdDao openIdDao;

    @Value("${conf.id}")
    private int voteId;
    @Value("${conf.rid}")
    private int voteRid;
    @Value("${conf.minTime}")
    private int minTime;
    @Value("${conf.maxTime}")
    private int maxTime;

    private static final String url = "https://4.hbcm666.cn//app/index.php?i=1&c=entry&id=%d&rid=%d&isopenlink=first&do=vote&m=tyzm_diamondvote&userss=&openid=%s";
    private static final String openIdUrl = "https://4.hbcm666.cn//app/index.php?i=1&c=entry&id=%d&rid=%d&isopenlink=first&do=viewc&m=tyzm_diamondvote&userss=";
    private static final String ridUrl = "https://4.hbcm666.cn//app/index.php?i=1&c=entry&rid=1&isopenlink=first&do=indexx&m=tyzm_diamondvote&userss=";
    private static final String playerUrl = "https://4.hbcm666.cn//app/index.php?i=1&c=entry&rid=%d&isopenlink=first&do=index&m=tyzm_diamondvote&userss=";
    private static final String playerInfoUrl = "https://4.hbcm666.cn//app/index.php?i=1&c=entry&id=%d&rid=%d&isopenlink=first&do=view&m=tyzm_diamondvote&userss=";

    @PostConstruct
    public void init() {
        this.toupiao();
    }

    public String createUrl(int rid, int id, String openId) {
        return String.format(url, id, rid, openId);
    }

    public String createOpenIdUrl(int rid, int id) {
        return String.format(openIdUrl, id, rid);
    }

    public String createPlayerInfoUrl(int rid, int id) {
        return String.format(playerInfoUrl, id, rid);
    }

    public String createPlayerUrl(int rid) {
        return String.format(playerUrl, rid);
    }

    public String vote(int rid, int id, String openId) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("act", "index");
        param.put("latitude", "0");
        param.put("longitude", "0");
        param.put("verify", "0");
        String body = getResponseBody(createUrl(rid, id, openId), param);

        JSON json = JSONUtil.parse(body);
        String msg = (String) json.getByPath("msg");
        json.putByPath("msg", UnicodeUtil.toString(msg));
        body = json.toString();
        return body;
    }

    public List<String> getAllOpenId(int rid, int id) {
        Map<String, Object> param = new HashMap<>();
        param.put("act", "index");
        param.put("limit", "1");
        String body = getResponseBody(createOpenIdUrl(rid, id), param);

        JSONArray content = JSONUtil.parse(body).getByPath("content", JSONArray.class);
        return content.getByPath("openid", List.class);
    }

    public List<String> getAllRid() {
        Map<String, Object> param = new HashMap<>();
        param.put("act", "index");
        String body = getResponseBody(ridUrl, param);

        JSONArray content = JSONUtil.parse(body).getByPath("content", JSONArray.class);
        return content.getByPath("rid", List.class);
    }

    public User getCurrentTicket(int rid, int id) {
        Map<String, Object> param = new HashMap<>();
        param.put("act", "index");
        String body = getResponseBody(createPlayerInfoUrl(rid, id), param);

        JSONObject voteuser = JSONUtil.parse(body).getByPath("voteuser", JSONObject.class);
        String name = voteuser.getByPath("name", String.class);
        Integer num = voteuser.getByPath("votenum", Integer.class);
        User user = new User(num, name);
        return user;
    }

    public List<String> getAllPlayerIdByRid(int rid) {
        ArrayList<String> list = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("act", "ajax_list");
        param.put("keyword", "");

        int i = 1;
        while (true) {
            param.put("limit", String.valueOf(i++));
            String body = getResponseBody(createPlayerUrl(rid), param);
            if (JSONUtil.parse(body).getByPath("status", Integer.class).equals(-103)) {
                break;
            }
            JSONArray content = JSONUtil.parse(body).getByPath("content", JSONArray.class);
            List ids = content.getByPath("id", List.class);
            list.addAll(ids);
        }
        return list;
    }

    public String getResponseBody(String url, Map<String, Object> param) {
        return HttpRequest.post(url)
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat")
                .header(Header.REFERER, "https://servicewechat.com/wx56749f567cc5ae85/1/page-frame.html")
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(param)
                .timeout(20000)
                .execute().body();
    }

    public void tou() throws IOException {
        List<String> allRid = getAllRid();
        for (String rid : allRid) {
            List<String> playerIdList = getAllPlayerIdByRid(Integer.parseInt(rid));
            for (String id : playerIdList) {
                List<String> allOpenId = getAllOpenId(Integer.parseInt(rid), Integer.parseInt(id));
                for (String openId : allOpenId) {
                    openIdDao.insert(openId);
                }
            }

        }
    }

    public List<OpenId> getAll() {
        List<OpenId> openIds = openIdDao.getAllOpenId();
//        List<OpenId> list = openIds.stream().distinct().collect(Collectors.toList());
        return openIds;
    }

    @Transactional
    public void insert(List<String> openids) {
        for (String openid : openids) {
            openIdDao.insert(openid);
        }
    }


    public void toupiao() {
        User start = getCurrentTicket(voteRid, voteId);
        long startTime = System.currentTimeMillis();
        List<OpenId> openIds = openIdDao.getAllOpenId();
        Collections.shuffle(openIds);
        for (OpenId openId : openIds) {
            String body = null;
            int time = RandomUtil.randomInt(minTime, maxTime);
            try {
                User user = getCurrentTicket(voteRid, voteId);
                log.info("姓名: {}, 票数: {} ", user.getName(), user.getNum());
                body = vote(voteRid, voteId, openId.getOpenId());
                log.info("随机时间:{}秒, 投票成功：id: {}, openId: {}, msg: {}", time / 1000, openId.getId(), openId, body);
                if (body.contains("锁定")) {
                    log.info("用户被锁定: 共刷 {} 票", user.getNum() - start.getNum());
                    log.info("\n共耗时 {} 分钟 {} 秒", (System.currentTimeMillis() - startTime) / 60000, (System.currentTimeMillis() - startTime) % 60000 / 1000);
                    break;
                }
                Thread.sleep(time);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
