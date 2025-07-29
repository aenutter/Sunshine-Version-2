-- select dogs.name, dog_id from dogs INNER JOIN activities ON dogs.dog_id = activities.activities_id
-- insert into dogs values 'Mandy', '1', 'Tapioca';
-- INSERT INTO dogs (location, kennel_number, name)
-- VALUES('Mandy', '28', 'Gigawatt');
SELECT
  location,
  kennel_number,
  name
FROM
  dogs
ORDER BY
  location DESC,
  kennel_number ASC;