### 추가로 실행해야 할 것

```postgresql
ALTER TABLE tb_site ADD CONSTRAINT fk_tb_site_parent FOREIGN KEY (parent_id) REFERENCES tb_site (id);
ALTER TABLE tb_category ADD CONSTRAINT fk_tb_category_parent FOREIGN KEY (parent_id) REFERENCES tb_category (id);
ALTER TABLE tb_article ADD CONSTRAINT fk_tb_article_parent FOREIGN KEY (parent_id) REFERENCES tb_article (id);
ALTER TABLE tb_reply ADD CONSTRAINT fk_tb_reply_parent FOREIGN KEY (parent_id) REFERENCES tb_reply (id);
ALTER TABLE tb_menu ADD CONSTRAINT fk_tb_menu_parent FOREIGN KEY (parent_id) REFERENCES tb_menu (id);
ALTER TABLE tb_code ADD CONSTRAINT fk_tb_code_parent FOREIGN KEY (parent_id) REFERENCES tb_code (id);
```