UPDATE event set cancelled=FALSE;

UPDATE applicants AS a 
SET    created_date = e.created_date, 
       created_by = u.email, 
       last_modified_date = NOW(), 
       last_modified_by = 'system' 
FROM   event e, 
       users u 
WHERE  a.event_id = e.event_id 
       AND a.user_id = u.user_id;