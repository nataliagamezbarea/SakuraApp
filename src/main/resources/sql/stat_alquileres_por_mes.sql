SELECT DATE_FORMAT(rental_date,'%Y-%m') as mes, COUNT(*) as alquileres
FROM rental
GROUP BY DATE_FORMAT(rental_date,'%Y-%m');