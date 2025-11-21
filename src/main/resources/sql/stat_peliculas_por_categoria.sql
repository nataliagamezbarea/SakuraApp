SELECT c.name as categoria, COUNT(*) as peliculas
FROM film_category fc
JOIN category c ON fc.category_id = c.category_id
GROUP BY c.name;