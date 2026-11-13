# Contributing to AnimeFlix

Thanks for considering a contribution!

## Before you start
- Read the project README and check existing issues.
- For larger changes, open an issue first so we can agree on the approach.
- Keep anime metadata sourced from the Jikan API or another clearly documented source.
- Only add links to official, authorized streams. Do not add pirated streams or misleading playback links.
- Never commit API secrets, signing keys, local configuration, or personal data.

## Development
1. Fork the repository and create a focused branch.
2. Make one small, clearly scoped change.
3. Run the relevant checks:
   ```bash
   gradle test
   gradle assembleDebug
   ```
4. Update documentation when behavior or setup changes.
5. Open a pull request describing the change and how it was tested.

## Pull request checklist
- [ ] The change has a clear purpose.
- [ ] Existing behavior is preserved unless the change intentionally updates it.
- [ ] Tests pass, or any limitation is explained.
- [ ] No secrets or unauthorized streaming links are included.
- [ ] Screenshots are included for visible UI changes.
