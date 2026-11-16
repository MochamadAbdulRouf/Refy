import re, glob, os, subprocess, sys

os.makedirs('docs/diagrams', exist_ok=True)
blocks = []
for f in sorted(glob.glob('flows/*.md')):
    s = open(f, encoding='utf-8').read()
    for i, m in enumerate(re.findall(r'```mermaid\n(.*?)```', s, flags=re.S), 1):
        slug = os.path.splitext(os.path.basename(f))[0]
        path = f'docs/diagrams/{slug}-{i:02d}.mmd'
        open(path, 'w', encoding='utf-8').write(m)
        blocks.append((path, f'docs/diagrams/{slug}-{i:02d}.svg'))

fails = []
for src, out in blocks:
    r = subprocess.run(['npx', '--yes', '@mermaid-js/mermaid-cli', '-i', src, '-o', out,
                        '-b', 'transparent', '-q'],
                       capture_output=True, text=True, shell=(os.name == 'nt'))
    if r.returncode != 0:
        fails.append((src, (r.stderr or r.stdout)[-500:]))
    print(('OK  ' if r.returncode == 0 else 'FAIL'), src)

print('\nTOTAL', len(blocks), 'diagrams,', len(fails), 'failed')
for f, err in fails:
    print('----', f, '\n', err)
