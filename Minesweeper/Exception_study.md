# 예외 처리 공부 중..
프로그래밍의 오류 종류에는
1. 논리 에러 : 실행되지만 의도와 다르게 작동 --> 버그
2. 컴파일 에러 : 컴파일시에 발생하는 에러 --> 구문 오류 
3. 런타임 에러 : 실행시에 발생하는 에러 --> 실행 오류

3 에서 에러(error)와 예외(exception)으로 나뉜다
3-1. 에러 : 메모리 부족(OutOfMemoryError), 스택오버플로우(StackOverflowError) 
           --> JVM 실행에 문제 발생 
           --> 개발자가 대처 불가능
3-2. 예외 : 에러보다는 덜 심각한 오류.
           하지만 예외에 대한 오류 처리는 제대로 해야함 
           -> 대응코드 작성

           여기서 대응 코드가 예외 처리 문법 (try - catch)


## 예외 클래스의 계층 구조
- 최상위 클래스인 Object를 상속받은 Throwable 클래스를 오류와 예외 클래스가 상속받는다

여기서 오류는 일단 제외하고 예외 클래스를 보면
- 예외 클래스
1. Exception 및 하위 클래스 : 사용자의 실수와 같은 외적인 요인에 의해 발생하는 컴파일시 발생하는 예외
ex> IOException : 입 / 출력에 관한 예외
    FileNotFoundException : 존재하지 않는 파일의 이름을 입력
    ClassNotFoundException : 실수로 클래스의 이름을 잘못 기재
    DataFormatException : 입력한 데이터 형식이 잘못된 경우

2. RuntimeException 클래스 : 프로그래머의 실수로 발생하는 예외
ex> ArithmeticException : 어떤 수를 0으로 나누는 것과 같이 비정상 계산 중 발생
    NullPointerException : NULL 객체 참조 시 발생
    IllegalArgumentException : 매소드의 전달 인자값이 잘못된 경우 발생
    IllegalStateException : 객체의 상태가 메소드 호출에는 부적합할 경우 발생
    IndexOutOfBoundsException : Index 값이 범위를 넘어갈 경우 발생
    UnsupportedOperationException : 객체가 메소드를 지원하지 않은 경우 발생
    SecurityException : 보안 위반 발생 시 보안 관리 프로그램에서 발생
    ProviderException : 구성 공급자 오류 시 발생
    NoSuchElementException : 구성 요소가 그 이상 없는 경우 발생
    ArrayStoreException : 객체 배열에 잘못된 객체 유형 저장 시 발생
    ClassCastException : 클래스 간의 형 변환 오류 시 발생
    EmptyStackException : 스택이 비어있는데 요소를 제거하려고 할 시 발생
    InputMismatchException : 의도치 않은 입력 오류 시 발생


### Checked Exception / Unchecked Exception
- Checked Exception 은 컴파일 예외 클래스, Unchecked Exception은 런타임 예외 클래스를 가리킨다.

- 똑같은 분류를 Checked / Unchecked 로 재분류한 이유 : 코드적 관점에서 예외 처리 동작을 필수 지정 유무 판별. -> Checked Exception은 반드시 예외 처리를 해야함.

# 예외 처리 (try - catch 문)
try 블록에는 예외발생 가능 코드가 위치 
if 코드에 오류가 발생
-> 오류 종류(예외 클래스)에 맞는 catch 문으로 가서 catch 블록 안에 있는 코드를 실행 (try 블록에서는 더이상 진행되지 않음)

if 오류가 발생 X 
-> catch 문은 실행 X

