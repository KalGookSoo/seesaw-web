WITH resultset AS (
    WITH cte AS (
        WITH extracted_images AS (SELECT id, REGEXP_MATCHES(content, '<img[^>]*src="([^"]+)"', 'g') AS image_src FROM tb_article)
        SELECT
            id AS article_id,
            image_src[1] AS image_url,
            regexp_replace(image_src[1], '.*/(.+\.(jpeg|png))$', '\1') AS original_name,
            gen_random_uuid() AS name_prefix
        FROM extracted_images
    )
    SELECT cte.*,
        CASE WHEN cte.original_name LIKE '%.jpeg' THEN 'image/jpeg' WHEN cte.original_name LIKE '%.png' then 'image/png' END AS mime_type,
        name_prefix || '_' || original_name AS name
    FROM cte
)
SELECT format($$INSERT INTO tb_attachment (id, created_by, created_date, created_ip, last_modified_by, last_modified_date, last_modified_ip, version, mime_type, name, original_name, path_name, reference_id, size) VALUES ('%s', 'admin', now(), '127.0.0.1', null, null, null, 0, '%s', '%s', '%s', '/', '%s', 0);$$, gen_random_uuid(), mime_type, name, original_name, article_id) AS sql,
       image_url,
       name
FROM resultset;
