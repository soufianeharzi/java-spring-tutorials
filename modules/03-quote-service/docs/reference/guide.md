# Spring Guide Reference

This module is the provider side of the [Consuming a RESTful Web Service](https://spring.io/guides/gs/consuming-rest/) guide.

## Official Guide

The official Spring guide focuses on the consumer side (making HTTP requests), but requires a backend service to call. This module provides that backend.

**Guide URL:** https://spring.io/guides/gs/consuming-rest/

## Related Guides

- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/) - How to create REST endpoints (used in this module)
- [Spring REST Docs](https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/) - API documentation

## What This Module Demonstrates

This module demonstrates concepts from the "Building a RESTful Web Service" guide:

1. **@RestController** - Creating REST endpoints
2. **Java Records** - Immutable DTOs for JSON responses
3. **List.of()** - Immutable collections for test data
4. **ThreadLocalRandom** - Thread-safe random selection

See [../concepts/quote-controller.md](../concepts/quote-controller.md) for detailed explanations.
