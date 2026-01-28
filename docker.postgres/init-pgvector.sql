-- Enable pgvector extension
-- PostgreSQL 시작 시 자동으로 vector 확장 활성화
-- Docker의 /docker-entrypoint-initdb.d/ 디렉토리에 마운트되어 자동 실행
CREATE EXTENSION IF NOT EXISTS vector;