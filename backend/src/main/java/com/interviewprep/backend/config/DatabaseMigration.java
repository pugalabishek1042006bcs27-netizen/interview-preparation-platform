package com.interviewprep.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.UpdateResult;

@Component
public class DatabaseMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigration.class);

    private final MongoTemplate mongoTemplate;

    public DatabaseMigration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        fixArraySkillAnalysis();
    }

    /**
     * Fixes documents where skillAnalysis was incorrectly stored as [] (array)
     * instead of a document/object. Clears the field so it deserializes as null.
     */
    private void fixArraySkillAnalysis() {
        Query query = new Query(Criteria.where("skillAnalysis").type(4)); // BSON type 4 = Array
        Update update = new Update().unset("skillAnalysis");
        UpdateResult result = mongoTemplate.updateMulti(query, update, "users");
        if (result.getModifiedCount() > 0) {
            log.info("Migration: fixed skillAnalysis array->null on {} user document(s)",
                    result.getModifiedCount());
        }
    }
}
