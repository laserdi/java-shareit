package ru.practicum.shareit.request.mapper;

//@AutoConfigureMockMvc
class ItemRequestDtoWithAnswersMapperTest {
//    @Spy
//    private final ItemRequestDtoWithAnswersMapper itemRequestDtoWithAnswersMapper;
//    ItemRequest itemRequest;
//    ItemRequestDtoWithAnswers itemRequestDtoWithAnswers;
//    LocalDateTime now;
//    Item item;
//    User owner;
//    User requester;
//
//    ItemRequestDtoWithAnswersMapperTest(ItemRequestDtoWithAnswersMapper itemRequestDtoWithAnswersMapper) {
//        this.itemRequestDtoWithAnswersMapper = itemRequestDtoWithAnswersMapper;
//    }
//
//    @BeforeEach
//    void setUp() {
//        now = LocalDateTime.now();
//        owner = User.builder().id(1L).name("username").email("email@wede.u").build();
//        requester = User.builder().id(2L).name("username").email("email@wede.u").build();
//        Comment comment = new Comment(1L, "content comment 1", item, requester, now);
//        item = Item.builder().id(1L).owner(owner).name("name for item").description("desc")
//                .comments(List.of(comment))
//                .available(true).build();
//        comment.setItem(item);
//
//        requester.setComments(List.of(comment));
//        itemRequest = ItemRequest.builder()
//                .id(1L)
//                .description("desc")
//                .requester(requester)
//                .created(now)
//                .items(List.of(item)).build();
//
//    }

//    @Test
//    void testMapToModel() {
//    }

//    @Test
//    void testMapToDto_and_testMapToModel() {
//        itemRequestDtoWithAnswers = itemRequestDtoWithAnswersMapper.mapToDto(itemRequest);
//        System.out.println(itemRequestDtoWithAnswers);
//        assertEquals(itemRequest.getId(), itemRequestDtoWithAnswers.getId());
//        assertEquals(itemRequest.getRequester().getName(), itemRequestDtoWithAnswers.getRequester().getName());
//        assertEquals(itemRequest.getDescription(), itemRequestDtoWithAnswers.getDescription());
//        assertEquals(itemRequest.getCreated(), itemRequestDtoWithAnswers.getCreated());
//        assertEquals(itemRequest.getItems().size(), itemRequestDtoWithAnswers.getItems().size());
//
//        ItemRequest model = itemRequestDtoWithAnswersMapper.mapToModel(itemRequestDtoWithAnswers);
//        assertEquals(itemRequestDtoWithAnswers.getId(), model.getId());
//        assertEquals(itemRequestDtoWithAnswers.getRequester().getName(), model.getRequester().getName());
//        assertEquals(itemRequestDtoWithAnswers.getDescription(), model.getDescription());
//        assertEquals(itemRequestDtoWithAnswers.getCreated(), model.getCreated());
//        assertEquals(itemRequestDtoWithAnswers.getItems().size(), model.getItems().size());
//    }

//    @Test
//    void testMapToDto_and_testMapToModel_whenIsNull() {
//        itemRequestDtoWithAnswers = itemRequestDtoWithAnswersMapper.mapToDto(null);
//        assertNull(itemRequestDtoWithAnswers);
//
//        ItemRequest model = itemRequestDtoWithAnswersMapper.mapToModel(null);
//        assertNull(model);
//    }
}