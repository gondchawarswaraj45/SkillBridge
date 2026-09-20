# No Annotations Rule

Do NOT use Java annotations anywhere in this codebase:
- Do not use `@Override`
- Do not use `@SuppressWarnings`
- Do not use `@SafeVarargs`
- Do not use `@Test`, `@BeforeEach`, `@BeforeAll`, `@DisplayName`
- Do not use any dependency-injection, ORM, or framework annotations (such as `@Autowired`, `@Component`, `@Entity`, `@Getter`, `@Setter`, etc.)

All code must be written in pure, standard Java without annotation syntax.
