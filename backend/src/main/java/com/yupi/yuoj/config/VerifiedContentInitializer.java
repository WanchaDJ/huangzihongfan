package com.yupi.yuoj.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yuoj.model.entity.MemberBenefit;
import com.yupi.yuoj.model.entity.Photo;
import com.yupi.yuoj.model.entity.PhotoCategory;
import com.yupi.yuoj.model.entity.Product;
import com.yupi.yuoj.service.MemberBenefitService;
import com.yupi.yuoj.service.PhotoCategoryService;
import com.yupi.yuoj.service.PhotoService;
import com.yupi.yuoj.service.ProductService;
import com.yupi.yuoj.utils.UploadPathUtils;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VerifiedContentInitializer implements ApplicationRunner {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${app.public-api-base-url:}")
    private String publicApiBaseUrl;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private ProductService productService;

    @Resource
    private MemberBenefitService memberBenefitService;

    @Resource
    private PhotoCategoryService photoCategoryService;

    @Resource
    private PhotoService photoService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ensureProductFavourTable();
            seedProducts();
            seedMemberBenefits();
            seedGallery();
        } catch (Exception e) {
            log.warn("verified content initialization skipped", e);
        }
    }

    private void ensureProductFavourTable() {
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS product_favour ("
                        + "id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'id',"
                        + "productId BIGINT NOT NULL COMMENT '商品 id',"
                        + "userId BIGINT NOT NULL COMMENT '用户 id',"
                        + "createTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + "updateTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                        + "PRIMARY KEY (id),"
                        + "UNIQUE KEY uk_product_user (productId, userId),"
                        + "KEY idx_userId (userId),"
                        + "KEY idx_productId (productId)"
                        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品收藏'");
    }

    private void seedProducts() {
        String albumCover = copySeedImage("除了快乐禁止入内.jpg", "album-happy-forbidden.jpg");
        String lightStickCover = copySeedImage("应援棒.jpg", "light-stick.jpg");
        String tshirtCover = copySeedImage("应援T恤.jpg", "support-tshirt.jpg");
        String calendarCover = copySeedImage("台历.jpg", "calendar.jpg");
        String figureCover = copySeedImage("手办.jpg", "figure.jpg");
        String postcardCover = copySeedImage("明信片.jpg", "postcard.jpg");

        upsertProduct(
                "黄子弘凡《除了快乐禁止入内》实体专辑",
                "根据公开发售物料和站内已整理资料制作的实体专辑展示商品，适合收藏和留念。",
                new BigDecimal("188.00"),
                new BigDecimal("188.00"),
                2000,
                albumCover,
                "merch",
                "实体专辑"
        );
        upsertProduct(
                "黄子弘凡官方应援棒",
                "演出现场常见的应援周边类型，站内商品图使用本地真实素材。",
                new BigDecimal("188.00"),
                new BigDecimal("188.00"),
                1200,
                lightStickCover,
                "merch",
                "应援周边"
        );
        upsertProduct(
                "黄子弘凡应援T恤",
                "会员中心补充的服饰类周边展示商品，适合线下活动和日常穿着。",
                new BigDecimal("99.00"),
                new BigDecimal("129.00"),
                900,
                tshirtCover,
                "merch",
                "服饰"
        );
        upsertProduct(
                "黄子弘凡主题台历",
                "以公开活动与写真素材为基础整理的台历类周边展示商品。",
                new BigDecimal("69.00"),
                new BigDecimal("89.00"),
                1200,
                calendarCover,
                "merch",
                "生活周边"
        );
        upsertProduct(
                "黄子弘凡手办摆件",
                "收藏向展示商品，封面图使用仓库中的真实手办素材。",
                new BigDecimal("299.00"),
                new BigDecimal("329.00"),
                300,
                figureCover,
                "merch",
                "收藏周边"
        );
        upsertProduct(
                "黄子弘凡明信片套装",
                "写真与舞台主题的纸品周边展示商品，适合作为签名和寄语留存。",
                new BigDecimal("39.00"),
                new BigDecimal("49.00"),
                1800,
                postcardCover,
                "merch",
                "纸品周边"
        );
    }

    private void upsertProduct(String name, String description, BigDecimal price, BigDecimal originalPrice,
            int stock, String coverImage, String type, String category) {
        Product product = productService.getOne(new QueryWrapper<Product>().eq("name", name).last("limit 1"));
        Date now = new Date();
        if (product == null) {
            product = new Product();
            product.setCreateTime(now);
            product.setSoldCount(0);
        }
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setOriginalPrice(originalPrice);
        product.setStock(stock);
        product.setCoverImage(coverImage);
        product.setImages(coverImage);
        product.setType(type);
        product.setCategory(category);
        product.setStatus(1);
        product.setUpdateTime(now);
        productService.saveOrUpdate(product);
    }

    private void seedMemberBenefits() {
        upsertBenefit("会员商品购买", "可在会员中心购买实体专辑、应援棒、服饰和纸品周边。", "shopping-bag", "regular,gold,platinum", 1);
        upsertBenefit("积分与成长值", "订单支付后自动累计积分、成长值和消费记录。", "sparkles", "regular,gold,platinum", 2);
        upsertBenefit("论坛私信", "可从论坛与其他登录用户发起私信会话。", "message-circle", "regular,gold,platinum", 3);
        upsertBenefit("个人相册上传", "可在相册页面上传并保存自己的图片。", "image-plus", "regular,gold,platinum", 4);
    }

    private void upsertBenefit(String name, String description, String icon, String levels, int sortOrder) {
        MemberBenefit benefit = memberBenefitService.getOne(new QueryWrapper<MemberBenefit>().eq("name", name).last("limit 1"));
        Date now = new Date();
        if (benefit == null) {
            benefit = new MemberBenefit();
            benefit.setCreateTime(now);
        }
        benefit.setName(name);
        benefit.setDescription(description);
        benefit.setIcon(icon);
        benefit.setLevels(levels);
        benefit.setSortOrder(sortOrder);
        benefit.setUpdateTime(now);
        memberBenefitService.saveOrUpdate(benefit);
    }

    private void seedGallery() {
        PhotoCategory stage = upsertCategory("舞台现场", "演唱会、音乐节与舞台表演照片", 1);
        PhotoCategory album = upsertCategory("专辑宣传", "专辑封面、宣传照与签名会素材", 2);
        PhotoCategory portrait = upsertCategory("公开写真", "杂志、写真和人物近景图片", 3);
        upsertCategory("用户上传", "登录用户自行上传的相册图片", 99);

        upsertPhoto("演唱会现场", "黄子弘凡演唱会现场公开图片", "演唱会现场.jpg", "concert-live.jpg", stage);
        upsertPhoto("舞台表演", "黄子弘凡舞台演出公开图片", "演唱会现场 舞台表演.jpg", "stage-performance.jpg", stage);
        upsertPhoto("《除了快乐禁止入内》专辑封面", "黄子弘凡个人专辑公开封面素材", "除了快乐禁止入内.jpg", "album-cover.jpg", album);
        upsertPhoto("杂志拍摄", "黄子弘凡公开杂志拍摄素材", "杂志拍摄.jpg", "magazine-photo.jpg", portrait);
    }

    private PhotoCategory upsertCategory(String name, String description, int sortOrder) {
        PhotoCategory category = photoCategoryService.getOne(new QueryWrapper<PhotoCategory>().eq("name", name).last("limit 1"));
        Date now = new Date();
        if (category == null) {
            category = new PhotoCategory();
            category.setCreateTime(now);
        }
        category.setName(name);
        category.setDescription(description);
        category.setSortOrder(sortOrder);
        category.setUpdateTime(now);
        photoCategoryService.saveOrUpdate(category);
        return category;
    }

    private void upsertPhoto(String title, String description, String sourceName, String targetName, PhotoCategory category) {
        Photo photo = photoService.getOne(new QueryWrapper<Photo>().eq("title", title).last("limit 1"));
        Date now = new Date();
        if (photo == null) {
            photo = new Photo();
            photo.setCreateTime(now);
        }
        String imageUrl = copySeedImage(sourceName, targetName);
        photo.setTitle(title);
        photo.setDescription(description);
        photo.setImageUrl(imageUrl);
        photo.setThumbnailUrl(imageUrl);
        photo.setCategoryId(category.getId());
        photo.setCategoryName(category.getName());
        photo.setSubCategory("公开资料");
        photo.setSortOrder(0);
        photo.setLikeCount(photo.getLikeCount() == null ? 0 : photo.getLikeCount());
        photo.setViewCount(photo.getViewCount() == null ? 0 : photo.getViewCount());
        photo.setUpdateTime(now);
        photoService.saveOrUpdate(photo);
    }

    private String copySeedImage(String sourceName, String targetName) {
        try {
            Path workspaceRoot = UploadPathUtils.getWorkspaceRoot();
            Path source = workspaceRoot.resolve(Paths.get("src", "assets", "黄子弘凡", sourceName)).normalize();
            Path target = UploadPathUtils.getUploadRoot().resolve(Paths.get("seed", targetName)).normalize();
            Files.createDirectories(target.getParent());
            if (Files.exists(source)) {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return getLocalUploadPrefix() + targetName;
        } catch (Exception e) {
            log.warn("seed image copy failed: {}", sourceName, e);
            return "";
        }
    }

    private String getLocalUploadPrefix() {
        String normalizedPublicBaseUrl = publicApiBaseUrl == null ? "" : publicApiBaseUrl.trim();
        if (!normalizedPublicBaseUrl.isEmpty()) {
            if (normalizedPublicBaseUrl.endsWith("/")) {
                normalizedPublicBaseUrl = normalizedPublicBaseUrl.substring(0, normalizedPublicBaseUrl.length() - 1);
            }
            return normalizedPublicBaseUrl + "/uploads/seed/";
        }
        String normalizedContextPath = contextPath == null ? "" : contextPath.trim();
        if (normalizedContextPath.endsWith("/")) {
            normalizedContextPath = normalizedContextPath.substring(0, normalizedContextPath.length() - 1);
        }
        if (!normalizedContextPath.isEmpty() && !normalizedContextPath.startsWith("/")) {
            normalizedContextPath = "/" + normalizedContextPath;
        }
        return normalizedContextPath + "/uploads/seed/";
    }
}
