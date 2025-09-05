# Minesweeper (지뢰찾기)

자바 콘솔 기반 지뢰찾기 프로젝트입니다.

## 기능
- 랜덤 지뢰 배치
- 첫 OPEN 안전 보장
- 칸 열기 / 깃발 표시 / 폭탄 표시 (폭탄이 있는 칸 열었을 시 폭발 표시)
- 깃발 표시한 걸 제외한 남은 폭탄 수 표시 (게임 종료 조건과는 상관 X) + 0일 경우 flag 금지
- 열려있는 칸의 숫자와 주위에 깃발 표시한 칸의 수가 같을 때, 그 칸에 인접한 8칸 전부 열기
- 승리 & 패배 판정

## 실행 방법
```bash
javac newversion/newversion/Refactoring.java
java newversion/newversion/main





