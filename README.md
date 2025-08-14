# rag_ai 프로젝트 아키텍처 가이드

## 📋 개요
rag_ai 프로젝트는 문서 기반 질의응답 시스템으로, 계층형 아키텍처를 통해 각 컴포넌트의 책임을 명확히 분리했습니다.

---

## 🏗️ 패키지 구조

### 📁 config
**역할**: 애플리케이션 설정 및 외부 연동 구성

**주요 컴포넌트**:
- `KafkaConfig`: 메시지 큐 설정
- `OpenAiProperties`: OpenAI API 연동 설정

**책임**:
- 시스템 전역 설정 관리
- 외부 서비스 연동 구성
- 빈 설정 및 의존성 주입

---

### 📁 domain
**역할**: 핵심 비즈니스 엔티티 정의

**주요 컴포넌트**:
- `GuideChunk`: 가이드 문서 청크 엔티티

**책임**:
- 데이터베이스 테이블과 매핑되는 엔티티 정의
- 비즈니스 규칙 및 제약사항 포함
- 데이터 구조의 표준화

---

### 📁 repository
**역할**: 데이터 접근 계층

**주요 컴포넌트**:
- `GuideChunkRepository`: 가이드 청크 데이터 접근 인터페이스

**책임**:
- 데이터베이스 CRUD 연산
- 쿼리 메서드 정의
- 벡터 검색 기능 구현

---

### 📁 client
**역할**: 외부 API 통신 계층

**주요 컴포넌트**:
- `openai` 패키지: OpenAI API 클라이언트
- OpenAI 관련 DTO 클래스들

**책임**:
- 외부 API 호출 및 응답 처리
- API 에러 핸들링
- 요청/응답 데이터 변환

---

### 📁 service
**역할**: 비즈니스 로직 계층

**주요 컴포넌트**:
- `RagService`: RAG 시스템 핵심 로직
- `DocxIngestService`: 문서 수집 처리 서비스
- `DocxIngestWorker`: 비동기 문서 처리 작업자

**책임**:
- 비즈니스 로직 구현
- 트랜잭션 관리
- 외부 서비스와의 조합 로직

---

### 📁 web (controller)
**역할**: 프레젠테이션 계층

**주요 컴포넌트**:
- `RagController`: RAG API 엔드포인트

**책임**:
- HTTP 요청 처리
- 요청 검증
- 응답 포맷팅

---

### 📁 dto
**역할**: 데이터 전송 객체 정의

**주요 컴포넌트**:
- `RagSuggestRequest`: RAG 요청 DTO
- `RagSuggestResponse`: RAG 응답 DTO
- `IngestRequestMessage`: 내부 메시지 DTO

**책임**:
- API 요청/응답 데이터 구조 정의
- 데이터 검증 규칙 정의
- 계층 간 데이터 전송 표준화

---

## 🔄 시스템 흐름

### RAG 질의응답 프로세스
1. **클라이언트 요청** → `RagController`에서 HTTP 요청 수신
2. **요청 검증** → DTO를 통한 데이터 유효성 검사
3. **비즈니스 로직** → `RagService`에서 RAG 로직 실행
4. **데이터 조회** → `GuideChunkRepository`에서 관련 문서 검색
5. **외부 API 호출** → `OpenAI Client`를 통한 LLM 요청
6. **응답 생성** → 결과를 DTO로 포맷팅하여 반환

### 문서 수집 프로세스
1. **문서 업로드** → `RagController`에서 파일 수신
2. **비동기 처리** → `DocxIngestWorker`에서 문서 처리
3. **텍스트 추출** → 문서 내용 파싱 및 청킹
4. **벡터 변환** → OpenAI API를 통한 임베딩 생성
5. **데이터 저장** → `GuideChunkRepository`를 통한 DB 저장

---

## 🎯 아키텍처 원칙

### 계층형 아키텍처 (Layered Architecture)
각 계층은 하위 계층에만 의존하며, 상위 계층을 알지 못합니다.

### 의존성 방향
```
Controller → Service → Repository → Domain
Controller → DTO
Service → Client → DTO
Service → Config
```

### 관심사 분리 (Separation of Concerns)
- **Controller**: HTTP 요청/응답 처리
- **Service**: 비즈니스 로직
- **Repository**: 데이터 접근
- **Client**: 외부 API 통신
- **DTO**: 데이터 전송
- **Domain**: 핵심 엔티티
- **Config**: 시스템 설정

---

## 📦 확장성 고려사항

### 새로운 기능 추가 시
1. **API 추가**: Controller → Service → Repository 순으로 구현
2. **외부 연동**: Client 패키지에 새로운 클라이언트 추가
3. **데이터 구조 변경**: Domain 엔티티 수정 후 Repository 업데이트
4. **설정 추가**: Config 패키지에 새로운 설정 클래스 추가

### 코드 컨벤션
- 각 패키지는 명확한 책임을 가짐
- 순환 의존성 금지
- 인터페이스 기반 의존성 주입 활용
- 예외 처리는 각 계층에서 적절히 수행

---

## 🛠️ 주요 기술 스택
- **Framework**: Spring Boot
- **Database**: PostgreSQL + pgvector
- **Message Queue**: Apache Kafka
- **External API**: OpenAI
- **Architecture Pattern**: Layered Architecture + DDD (Domain-Driven Design)
