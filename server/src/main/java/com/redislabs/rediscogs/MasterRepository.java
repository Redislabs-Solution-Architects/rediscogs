package com.redislabs.rediscogs;

import org.springframework.data.repository.CrudRepository;

public interface MasterRepository extends CrudRepository<RedisMaster, String> {

}