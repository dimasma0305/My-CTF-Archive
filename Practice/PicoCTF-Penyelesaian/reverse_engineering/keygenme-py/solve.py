import re
import hashlib

with open("keygenme-trial.py", "r") as f:
    DATA = f.read()


def get_key_full_template_trials():
    key = re.findall(r'key_part.*=.*', DATA)
    key_part_static1_trial = re.findall(r"(?<= \= \").*(?=\")", key[0])
    key_part_dynamic1_trial = re.findall(r"(?<= \= \").*(?=\")", key[1])
    key_part_static2_trial = re.findall(r"(?<= \= \").*(?=\")", key[2])
    key_full_template_trial = key_part_static1_trial + \
        key_part_dynamic1_trial + key_part_static2_trial
    return ''.join(key_full_template_trial)


def get_dynamic_trials_idx():
    idx = re.findall(
        r'(?<=hashlib.sha256\(username_trial\).hexdigest\(\)\[).*(?=\]:)', DATA)
    return [int(i) for i in idx]


def get_username_trial():
    username = re.findall(r'(?<=username_trial = ").*(?=")', DATA)[0]
    return bytes(username, 'utf-8')


def get_flag_parts(username, idx):
    part = ""
    for i in idx:
        part += hashlib.sha256(username).hexdigest()[i]
    return part


def merge_template_and_part(template: str, part):
    solution = template.replace("xxxxxxxx", part)
    return solution

template = get_key_full_template_trials()
idx = get_dynamic_trials_idx()
username = get_username_trial()
flag_part = get_flag_parts(username, idx)
print(merge_template_and_part(template, flag_part))
