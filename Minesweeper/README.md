# Minesweeper (Console) — Java

콘솔 기반 지뢰찾기 프로젝트. 객체지향 설계 및 첫 클릭 안전 기능을 포함합니다.
- 언어: Java
- 실행: javac / java (JDK 8+)

# 소개
콘솔에서 작동하는 자바 기반 지뢰찾기(Minesweeper)입니다.
객체지향적으로 Game / Board / Cell / Renderer / InputHandler / BoardInitializer / Command로 책임을 분리하며 리팩토링했습니다.

# 주요 목표
- 가독성 좋은 OOP 설계
- 사용자 경험(첫 클릭 안전, 남은 지뢰 수 표시),
- 유지보수와 확장성(초기화/입력/출력 분리)

# 핵심 기능
- 콘솔 환경에서 동작 (UTF-8 이모지 출력 포함)
- 난이도 선택: EASY / NORMAL / HARD
- 첫 클릭 안전 (첫 열기에서 절대 폭발하지 않음)
- 남은 지뢰 수(플래그 수 기반) 표시
- 세 가지 명령: Open(O), Flag(F), Around(A)
- A는 열린 칸에서 주변 깃발 수가 숫자와 같으면 주변 칸을 한 번에 여는 동작
- 게임 시간 측정 (초 단위 출력)
- 게임 종료 시 전체 보드 공개(맞게/틀리게 표시)

# 게임 규칙 / 조작법 (입력 예시)
- 난이도 선택: 프로그램 시작 후 E, N, H 입력
- Command 입력 형식:
    <row> <col> <command>
ex>
3 5 O     -> 3행 5열 열기 (Open)
4 2 F     -> 4행 2열 깃발 전환 (Flag)
2 2 A     -> 열린 칸 2,2에서 주변 깃발과 숫자를 확인 후 주변 자동 열기 (Around)

- 좌표는 1부터 시작 (내부적으로 -1 처리)
- 첫 열기(처음 O)는 안전처리(지뢰가 배치되지 않음)

# 구조와 클래스 설명 (요약)

## ewversion.Refactoring

- main(): Game 생성 및 실행 진입점

## Game

- 전체 게임 루프 관리

- InputHandler에서 난이도/명령을 받고 Board에 위임

- Timer로 플레이 시간 측정

## InputHandler

- 콘솔 입력 처리

- getLevel() / getCommand(Board) 제공 (입력 검증 포함)

## Board

- 2차원 Cell[][] grid 관리

- applyCommand(Command)로 명령 처리 (첫 클릭 시 BoardInitializer로 초기화)

- openCell, aroundCell, search 등 게임 로직 포함

- allOpen()으로 게임 종료 시 보드 공개

## BoardInitializer

- initialize(Board, safeRow, safeCol)

- 지뢰 배치 (safeRow,safeCol 제외) 및 주변 숫자 계산

## Cell

- 상태 CellState {CLOSED, OPENED, FLAGGED} 및 value(숫자, -1=mine, -2=exploded, -3=잘못깃발)

- 상태 전환 메서드 (open, flag, unflag 등)

## Renderer

- 보드 출력 (콘솔 포맷, 이모지 포함)

- 게임 종료 시 메시지 출력

## Command / ActionType

- 명령을 캡슐화 (row, col, action)

- action: OPEN, FLAG, AROUND


# 주요 구현 포인트
- 첫 클릭 안전: 첫 OPEN이 들어올 때까지 지뢰 배치를 미루고, 첫 좌표(또는 원하면 주변 8칸) 제외하여 배치 — 사용자 경험 개선.

- Initializer 분리: BoardInitializer로 분리해 Board의 생성 책임을 단순화(SRP 준수).

- 명령 캡슐화: Command 객체로 입력을 캡슐화하여 Board가 단일 진입점(applyCommand)을 갖도록 함.

- Cell 값 표현 규칙:
    -1 : 폭탄 (#)
    0..8 : 주변 폭탄 수
    -2 : 플레이어가 밟아서 폭발한 칸 (*)
    -3 : 잘못 깃발한 칸(깃발 표시했지만 폭탄 아님) (X)

- Renderer에서 게임 공개: board.isGameOver()이면 allOpen() 호출하도록 해서 종료 시 완전한 상태를 출력.


# 향후 개선 / 확장 아이디어
- GUI 전환: Swing / JavaFX로 마우스 클릭, 우클릭(깃발) 지원 

- 사용자 설정 난이도(CUSTOM) 및 설정 저장/로드

- 스코어보드(최고 기록 저장), 리플레이 저장

- 단위 테스트 (JUnit) 추가 — 핵심: BoardInitializer, openCell, arroundCell 테스트

- AI/추천: 현재 보드에서 안전한 칸을 추천하는 간단한 힌트 기능

