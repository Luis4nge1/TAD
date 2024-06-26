require('dotenv').config();
const promise = require('bluebird');
const options = {
    promiseLib: promise,
    query: (e) => {}
}

const pgp = require('pg-promise')(options);
const types = pgp.pg.types;
types.setTypeParser(1114,function(stringValue) {
    return stringValue;
});

const databaseConfig = process.env.POSTGRES_URL;

const db = pgp(databaseConfig);

module.exports = db;