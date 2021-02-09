CACHE_SIZE_LIMIT = 200

class Config(object):
    CACHE_SIZE = CACHE_SIZE_LIMIT

class ProductionConfig(Config):
    pass

class DevelopmentConfig(Config):
    CACHE_SIZE = CACHE_SIZE_LIMIT
    MODEL_NAME = 'CNN'

class TestingConfig(Config):
    CACHE_SIZE = 200