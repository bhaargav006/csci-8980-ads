class Config(object):
    CACHE_SIZE=100

class ProductionConfig(Config):
    pass

class DevelopmentConfig(Config):
    CACHE_SIZE=100
    MODEL_NAME='MLP'

class TestingConfig(Config):
    CACHE_SIZE=100