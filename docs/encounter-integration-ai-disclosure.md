# AI Use Disclosure — Encounter Integration

Team member: Guoqing Sun (`@Sunqing050114`)

Tool: OpenAI ChatGPT / Codex

## Scope of use

Generative AI was used for code, automated tests, documentation and GitHub task preparation for the
Team 2 encounter-integration subtask. The incorporated output is visible in the linked commits and
pull request; it should be reviewed as AI-assisted work rather than represented as independently
authored code.

## Prompts and incorporated output

The following user prompts were used in the relevant work sessions (Chinese retained verbatim):

1. `你不是连接我的github了吗 直接帮我完成 我的part`
2. `再次尝试完美完成`
3. `已经授权`
4. `先帮我完成我的部分 帮助我能拿满分`

Incorporated output:

- [Commit `2eea28b` — encounter integration implementation, tests and documentation](https://github.com/UQcsse3200/2026-studio-3/commit/2eea28b6ad1de7b47ebd548b2f4249c7900db5f1)
- [Pull request #69 — complete reviewable diff and discussion](https://github.com/UQcsse3200/2026-studio-3/pull/69)
- The follow-up disclosure, expanded test plan and controller edge-case tests included in pull
  request #69.

AI output was used to propose/refine the gateway-and-adapter structure, transactional rollback
behaviour, lifecycle edge-case tests, technical documentation and issue/PR wording. The linked diff
is the exact incorporated output and is the authoritative record if chat wording differs from the
final reviewed implementation.

## Verification and human review

- Reviewed the changed files and cross-team API mappings against the corresponding feature branches.
- Ran the repository's unit-test and format workflows. PR #69 recorded 257 successful tests and a
  successful Java format check for the initial commit.
- Added focused tests for atomic Player/Deck/stock changes, rollback, invalid starts, stale/duplicate
  callbacks and end-to-end Chance -> Shop -> Map behaviour.
- Kept manual test results explicitly marked `Not run` until they are performed on the final merged
  build; no manual evidence is inferred from unit tests.
- Requires Team 2 code review, final cross-team adapter replacement, a passing CI run on `main`, and
  all team members' confirmation in the Sprint Achievement Form before submission.

This page must be copied or linked from the relevant project Wiki page and summarised in the Sprint
Achievement Form AI Declaration. Other team members must add their own AI/tool uses separately and
must personally sign the final declaration.
