package com.yupi.yuoj.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yuoj.mapper.PostMapper;
import com.yupi.yuoj.mapper.UserMapper;
import com.yupi.yuoj.model.entity.Post;
import com.yupi.yuoj.model.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class PostDataInitializer implements CommandLineRunner {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PostMapper postMapper;

    private static final String DEFAULT_PASSWORD = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));

    private static final String[] NICKNAMES = {
            "黄子弘凡粉丝会长",
            "声之韵的小音符",
            "星光下的追光者",
            "超爱凡凡的小歌迷",
            "黄子弘凡的专属耳朵",
            "高音爱好者",
            "演唱会打卡达人"
    };

    private static final String[][] USER_POSTS = {
            {
                    "欢迎大家来到黄子弘凡粉丝论坛！很高兴能和各位喜欢凡凡的朋友们在这里相聚~",
                    "黄子弘凡的每一次舞台我都会反复刷好几遍，他的感染力真的太强了！",
                    "最近整理了凡凡所有的演出视频合集，满满几个硬盘都是宝藏！"
            },
            {
                    "最近循环凡凡的《声入人心》片段，他的音色太绝了！",
                    "凡凡唱歌的时候眼睛里有光啊谁懂！",
                    "音乐是连接我们和黄子弘凡最好的桥梁✨"
            },
            {
                    "从星光大道认识的凡凡，一路追光到现在，看着他越来越好太感动了！",
                    "每一个有凡凡歌声陪伴的夜晚都特别温暖",
                    "期待黄子弘凡更多的舞台和新歌！"
            },
            {
                    "今天又是单曲循环凡凡新歌的一天，完全停不下来！",
                    "有谁和我一样手机铃声全换成凡凡的歌了？举个手！",
                    "凡凡的每一首歌我都能一字不差背下来，太爱了！"
            },
            {
                    "黄子弘凡的专属耳朵报到！他的每一个转音细节我都反复品味",
                    "专业听歌人士表示，凡凡的唱功一直在进步，太稳了！",
                    "凡凡的现场真的比CD还要好听一万倍！"
            },
            {
                    "黄子弘凡的高音部分真的太震撼了，每次听都起鸡皮疙瘩！",
                    "学美声的我表示，凡凡的技术太扎实了，教科书级别的演唱！",
                    "高音爱好者表示凡凡的高音是我循环的动力！"
            },
            {
                    "刚从凡凡的演唱会回来，现场气氛太燃了！",
                    "今年已经打卡了3场黄子弘凡的演唱会，每一场都印象深刻！",
                    "分享一下我这次演唱会拍到的高清照片，全是美好回忆🥰"
            }
    };

    @Override
    public void run(String... args) {
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.eq("isDelete", 0);
        Long postCount = postMapper.selectCount(postQueryWrapper);
        if (postCount >= 20) {
            System.out.println("帖子数据已足够，跳过初始化");
            return;
        }
        
        System.out.println("开始初始化论坛虚拟用户和帖子...");

        Long[] userIds = new Long[NICKNAMES.length];

        for (int i = 0; i < NICKNAMES.length; i++) {
            String nickname = NICKNAMES[i];
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("userName", nickname).eq("isDelete", 0);
            User existUser = userMapper.selectOne(userQueryWrapper);
            if (existUser != null) {
                userIds[i] = existUser.getId();
                continue;
            }

            User newUser = new User();
            newUser.setUserName(nickname);
            newUser.setUserAccount("fan" + (i + 1));
            newUser.setUserPassword(DEFAULT_PASSWORD);
            newUser.setUserAvatar("https://picsum.photos/seed/fan" + (i + 1) + "/200/200");
            newUser.setUserProfile("黄子弘凡忠实粉丝");
            newUser.setUserRole("user");
            newUser.setCreateTime(new Date());
            newUser.setUpdateTime(new Date());
            newUser.setIsDelete(0);
            userMapper.insert(newUser);
            userIds[i] = newUser.getId();
        }

        for (int i = 0; i < NICKNAMES.length; i++) {
            for (String content : USER_POSTS[i]) {
                Post post = new Post();
                post.setTitle("分享");
                post.setContent(content);
                post.setTags("[]");
                post.setThumbNum((int) (Math.random() * 50) + 5);
                post.setFavourNum((int) (Math.random() * 20) + 1);
                post.setUserId(userIds[i]);
                post.setCreateTime(new Date());
                post.setUpdateTime(new Date());
                post.setIsDelete(0);
                postMapper.insert(post);
            }
        }

        System.out.println("论坛虚拟用户和帖子初始化完成！");
    }
}
