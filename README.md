# JavaJosa
자바 한글 조사 처리

## Introduction
- 파이썬 한글 조사 처리 [PyJosa](https://github.com/myevan/pyjosa) 패키지의 Java 구현.

## 추가 기능
- 조사 처리 규칙과 앞 단어 사이 띄어쓰기 제거.
- 조사 처리 규칙의 앞 단어가 숫자인 경우 처리 가능.

## Requirements
- Java (JDK 8+)

# 예제
#### 코드

	import JavaJosa;
	JavaJosa cls = new JavaJosa();
	String example = "5 (이)가 어떨까? 4(이)라능~ 수녀 (을)를 존경했어. 남자들 (을)를 입히다.";
	System.out.println(cls.getPostWord(example));